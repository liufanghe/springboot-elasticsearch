package cn.hm1006.search;

import cn.hm1006.search.entity.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SearchApplicationTests {
    public void contextLoads() {
    }

    @Autowired
    private RestHighLevelClient client;

    String index = "openapi_customer";
    String type = "customer";

    /**
     * 创建索引 openapi_customer
     * @throws IOException
     */
    @Test
    public void createIndex() throws IOException {
        //1. 准备关于索引的settings
        Settings.Builder settings = Settings.builder()
                .put("number_of_shards", 5)
                .put("number_of_replicas", 1);

        //2. 准备关于索引的结构mappings
        XContentBuilder mappings = JsonXContent.contentBuilder()
                .startObject()
                .startObject("properties")
                .startObject("id")
                .field("type", "integer")
                .endObject()
                .startObject("username")
                .field("type", "keyword")
                .endObject()
                .startObject("password")
                .field("type", "keyword")
                .endObject()
                .startObject("nickname")
                .field("type", "text")
                .endObject()
                .startObject("money")
                .field("type", "long")
                .endObject()
                .startObject("address")
                .field("type", "text")
                .endObject()
                .startObject("state")
                .field("type", "integer")
                .endObject()
                .endObject()
                .endObject();


        //3. 将settings和mappings封装到一个Request对象
        CreateIndexRequest request = new CreateIndexRequest(index)
                .settings(settings)
                .mapping(type, mappings);

        //4. 通过client对象去连接ES并执行创建索引
        CreateIndexResponse resp = client.indices().create(request, RequestOptions.DEFAULT);

        //5. 输出
        System.out.println("resp:" + resp.toString());
    }

    /**
     * 向openapi_customer添加数据
     * @throws IOException
     */
    @Test
    public void testAddData() throws IOException {
        //1. 准备多个json数据
        Customer c1 = new Customer();
        c1.setId(1);
        c1.setUsername("haier");
        c1.setPassword("111111");
        c1.setNickname("海尔集团");
        c1.setMoney(2000000L);
        c1.setAddress("青岛");
        c1.setState(1);

        Customer c2 = new Customer();
        c2.setId(2);
        c2.setUsername("lianxiang");
        c2.setPassword("111111");
        c2.setNickname("联想");
        c2.setMoney(1000000L);
        c2.setAddress("联想");
        c2.setState(1);

        Customer c3 = new Customer();
        c3.setId(3);
        c3.setUsername("google");
        c3.setPassword("111111");
        c3.setNickname("谷歌");
        c3.setMoney(1092L);
        c3.setAddress("霉果");
        c3.setState(1);

        ObjectMapper mapper = new ObjectMapper();

        String json1 = mapper.writeValueAsString(c1);
        String json2 = mapper.writeValueAsString(c2);
        String json3 = mapper.writeValueAsString(c3);

        //2. 创建Request，将准备好的数据封装进去
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest(index, type, c1.getId().toString()).source(json1, XContentType.JSON));
        request.add(new IndexRequest(index, type, c2.getId().toString()).source(json2, XContentType.JSON));
        request.add(new IndexRequest(index, type, c3.getId().toString()).source(json3, XContentType.JSON));

        //3. 用client执行
        BulkResponse resp = client.bulk(request, RequestOptions.DEFAULT);

        //4. 输出结果
        System.out.println(resp.toString());
    }



}
