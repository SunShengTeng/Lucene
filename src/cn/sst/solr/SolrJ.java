package cn.sst.solr;


import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.junit.Test;

/**
 * 
 * @ClassName: SolrJ
 * @Description: SolrJ，维护索引库（添加。删除。修改，查询）
 * @author: sunshengteng
 * @date: 2018年 3月9日 下午5:06:15
 */
public class SolrJ {

	// 注意这里的URL不要跟浏览器中访问的一致，不能是类似：http://192.168.169.170:8080/solr/index.html#/Core01
	final String baseSolrUrl = "http://192.168.169.170:8080/solr/Core01";

	/**
	 * 
	* @Title: getSolrClient  
	* @Description: 获取操作索引库的核心对象
	* @param @return    设定文件  
	* @return SolrClient    返回类型  
	* @throws
	 */
	private SolrClient getSolrClient() {
		// 创建solrClient同时指定超时时间，不指定走默认配置
		return new HttpSolrClient.Builder(baseSolrUrl).withConnectionTimeout(10000).withSocketTimeout(1000).build();

	}

	/**
	* @Title: textCreateIndex  
	* @Description: 添加索引
	* @param @throws Exception    设定文件  
	* @return void    返回类型  
	* @throws
	 */
	@Test
	public void testCreateIndex() throws Exception {
		// 获取solrClient
		SolrClient solrClient = getSolrClient();
		// 创建文档
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", "12313");
		document.addField("e3mall_product_title", "孙大爷");
		// 添加文档，查看响应结果
		UpdateResponse updateResponse = solrClient.add(document);
		System.out.println(updateResponse.getElapsedTime());

		// 提交
		solrClient.commit();
	}

	/**
	 * 
	* @Title: textDeleteIndex  
	* @Description: 删除索引
	* @param @throws Exception    设定文件  
	* @return void    返回类型  
	* @throws
	 */
	@Test
	public void testDeleteIndex() throws Exception {

		SolrClient solrClient = getSolrClient();
		solrClient.deleteByQuery("*:*");
		solrClient.commit();
	}
	/**
	 * 
	* @Title: testSearch  
	* @Description: 高级查询
	* @param @throws Exception    设定文件  
	* @return void    返回类型  
	* @throws
	 */
	@Test
	public void testSearch() throws Exception {
	
		SolrClient solrClient = getSolrClient();
		
		Map<String, String> queryMap = new HashMap<>();
		queryMap.put("q", "*:*");
		SolrParams SolrParams = new MapSolrParams(queryMap );
		QueryResponse queryResponse = solrClient.query(SolrParams );
		SolrDocumentList documentList = queryResponse.getResults();
		for (SolrDocument solrDocument : documentList) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("e3mall_product_sell_point"));
			System.out.println(solrDocument.get("e3mall_product_title"));
			System.out.println(solrDocument.get("e3mall_product_image"));
		}
		solrClient.close();
	}
	
}
