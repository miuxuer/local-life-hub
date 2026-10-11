package com.llh.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.util.NamedValue;
import com.llh.document.ProductDoc;
import com.llh.dto.ProductSearchDTO;
import com.llh.entity.Product;
import com.llh.mapper.ProductMapper;
import com.llh.result.PageResult;
import com.llh.service.ProductSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ProductSearchServiceImpl implements ProductSearchService {

    private static final String INDEX = "product";

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 根据关键字搜索商品
     */
    @Override
    public PageResult search(ProductSearchDTO productSearchDTO) {
        int page = productSearchDTO.getPage() < 1 ? 1 : productSearchDTO.getPage();
        int pageSize = productSearchDTO.getPageSize() < 1 ? 10 : productSearchDTO.getPageSize();

        try {
            createIndexIfAbsent();

            SearchResponse<ProductDoc> response = elasticsearchClient.search(s -> s
                            .index(INDEX)
                            .query(buildQuery(productSearchDTO))
                            .highlight(h -> h.fields(NamedValue.of("name", HighlightField.of(f -> f
                                    .preTags("<em>")
                                    .postTags("</em>")))))
                            .sort(so -> so.score(sc -> sc.order(SortOrder.Desc)))
                            .sort(so -> so.field(f -> f.field("id").order(SortOrder.Desc)))
                            .from((page - 1) * pageSize)
                            .size(pageSize),
                    ProductDoc.class);

            List<ProductDoc> list = new ArrayList<>();
            for (Hit<ProductDoc> hit : response.hits().hits()) {
                ProductDoc productDoc = hit.source();
                if (productDoc == null) {
                    continue;
                }

                //取出关键字在商品名称中的高亮片段
                Map<String, List<String>> highlight = hit.highlight();
                List<String> nameFragments = highlight.get("name");
                if (nameFragments != null && !nameFragments.isEmpty()) {
                    productDoc.setHighlightName(nameFragments.get(0));
                }

                list.add(productDoc);
            }

            long total = response.hits().total() == null ? list.size() : response.hits().total().value();

            return new PageResult(total, list);
        } catch (IOException e) {
            log.error("商品搜索失败,keyword:{}", productSearchDTO.getKeyword(), e);
            throw new RuntimeException("商品搜索失败", e);
        }
    }

    /**
     * 重建全部商品的索引
     */
    @Override
    public void syncAll() {
        try {
            createIndexIfAbsent();

            List<Product> products = productMapper.list(new Product());
            for (Product product : products) {
                index(product);
            }

            log.info("商品索引重建完成,共{}条", products.size());
        } catch (IOException e) {
            log.error("商品索引重建失败", e);
            throw new RuntimeException("商品索引重建失败", e);
        }
    }

    /**
     * 同步单个商品的索引
     */
    @Override
    public void syncProduct(Product product) {
        try {
            createIndexIfAbsent();
            index(product);
        } catch (Exception e) {
            //索引同步失败不能影响商品本身的增删改
            log.warn("同步商品索引失败,商品id:{}", product.getId(), e);
        }
    }

    /**
     * 删除商品的索引
     */
    @Override
    public void deleteByIds(List<Long> ids) {
        try {
            for (Long id : ids) {
                elasticsearchClient.delete(d -> d.index(INDEX).id(String.valueOf(id)));
            }
        } catch (Exception e) {
            //索引同步失败不能影响商品本身的增删改
            log.warn("删除商品索引失败,商品id:{}", ids, e);
        }
    }

    /**
     * 拼接查询条件：关键字在商品名称和描述中匹配，名称权重更高，分类和状态作为过滤条件
     */
    private Query buildQuery(ProductSearchDTO productSearchDTO) {
        List<Query> filters = new ArrayList<>();
        if (productSearchDTO.getStatus() != null) {
            filters.add(Query.of(q -> q.term(t -> t
                    .field("status")
                    .value(productSearchDTO.getStatus().longValue()))));
        }
        if (productSearchDTO.getCategoryId() != null) {
            filters.add(Query.of(q -> q.term(t -> t
                    .field("categoryId")
                    .value(productSearchDTO.getCategoryId().longValue()))));
        }

        if (!StringUtils.hasText(productSearchDTO.getKeyword())) {
            return Query.of(q -> q.bool(b -> b.filter(filters)));
        }

        return Query.of(q -> q.bool(b -> b
                .must(m -> m.multiMatch(mm -> mm
                        .query(productSearchDTO.getKeyword())
                        .fields("name^3", "description")))
                .filter(filters)));
    }

    /**
     * 把商品写入索引
     */
    private void index(Product product) throws IOException {
        ProductDoc productDoc = ProductDoc.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryId(product.getCategoryId())
                .price(product.getPrice() == null ? null : product.getPrice().doubleValue())
                .image(product.getImage())
                .description(product.getDescription())
                .status(product.getStatus())
                .build();

        elasticsearchClient.index(i -> i
                .index(INDEX)
                .id(String.valueOf(product.getId()))
                .document(productDoc));
    }

    /**
     * 索引不存在时按指定的字段映射创建
     */
    private void createIndexIfAbsent() throws IOException {
        boolean exists = elasticsearchClient.indices().exists(e -> e.index(INDEX)).value();
        if (exists) {
            return;
        }

        elasticsearchClient.indices().create(c -> c
                .index(INDEX)
                .mappings(m -> m
                        .properties("id", p -> p.long_(l -> l))
                        .properties("name", p -> p.text(t -> t.analyzer("standard")))
                        .properties("categoryId", p -> p.long_(l -> l))
                        .properties("price", p -> p.double_(d -> d))
                        .properties("image", p -> p.keyword(k -> k.index(false)))
                        .properties("description", p -> p.text(t -> t.analyzer("standard")))
                        .properties("status", p -> p.integer(i -> i))
                ));

        log.info("创建商品搜索索引:{}", INDEX);
    }

}
