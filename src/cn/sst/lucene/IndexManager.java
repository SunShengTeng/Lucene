/**
 * 注意：第一版使用的是最新的lucene（7.2版本）这个版本在使用IKAnalyzer总是跟Junit4.12有矛盾，故将lucene改成4.10版本
 */
package cn.sst.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * 
 * @ClassName: IndexManager
 * @Description: 索引库管理
 * @author: sunshengteng
 * @date: 2018年3月6日 下午3:55:01
 */
public class IndexManager {

	/**
	 * @Title: getIndexWriter @Description: 获取IndexWriter
	 * 
	 * @param @return
	 * @param @throws
	 *            Exception 设定文件 @return IndexWriter 返回类型 @throws
	 */
	public IndexWriter getIndexWriter() throws Exception {
		// 1、创建一个indexWriter对象
		// 指定一个索引库的位置
		// 指定一个分析器，对文档内容进行分析
		File fileDir = new File("/Users/sunshengteng/Java_project/Workspaces/Lucene/temp/index");
		Directory dir = FSDirectory.open(fileDir);
		Analyzer iKAnalyzer = new IKAnalyzer();
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST, iKAnalyzer);
		return new IndexWriter(dir, indexWriterConfig);
	}

	/**
	 * 
	 * @Title: getIndexSearcher @Description: @param @return @param @throws
	 *         Exception 设定文件 @return IndexSearcher 返回类型 @throws
	 */
	public IndexSearcher getIndexSearcher() throws Exception {
		// 1、指定索引库位置
		File file = new File("/Users/sunshengteng/Java_project/Workspaces/Lucene/temp/index");
		Directory indexDir = FSDirectory.open(file);
		// 2、创建indexReader对象(操作索引库的流对象)
		IndexReader indexReader = DirectoryReader.open(indexDir);
		// 3、创建indexSearcher对象（搜索对象）
		return new IndexSearcher(indexReader);
	}

	/**
	 * 
	 * @throws IOException 
	 * @Title: printableResult @Description: 渲染结果 @param 设定文件 @return void
	 * 返回类型 @throws
	 */
	public void printableResult(IndexSearcher indexSearcher,Query query) throws IOException {
		TopDocs topDocs = indexSearcher.search(query, 100);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;// 获取评分后的文档
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docId = scoreDoc.doc;// 获取文档的ID
			Document document = indexSearcher.doc(docId);// 根据文档ID查询文档
			// 6、渲染结果
			System.out.println(document.get("fileName"));
			System.out.println(document.get("filePath"));
			System.out.println(document.get("fileSize"));
			System.out.println(document.get("fileContent"));
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");

		}
	}

	/**
	 * @Title: deleteAllIndex @Description: 删除索引库所有索引 @param @throws Exception
	 *         设定文件 @return void 返回类型 @throws
	 */
	@Test
	public void deleteAllIndex() throws Exception {
		IndexWriter indexWriter = getIndexWriter();
		indexWriter.deleteAll();
		indexWriter.close();
	}

	/**
	 * 
	 * @Title: deletepartIndex @Description: 根据条件删除索引库的内容 @param @throws
	 *         Exception 设定文件 @return void 返回类型 @throws
	 */
	@Test
	public void deletepartIndex() throws Exception {
		IndexWriter indexWriter = getIndexWriter();
		Query query = new TermQuery(new Term("fileContent", "中"));
		indexWriter.deleteDocuments(query);
		indexWriter.close();
	}

	/**
	 * 
	 * @Title: updateIndex @Description: 更新索引（先删除后创建） @param @throws Exception
	 *         设定文件 @return void 返回类型 @throws
	 */
	@Test
	public void updateIndex() throws Exception {
		IndexWriter indexWriter = getIndexWriter();
		Document doc = new Document();
		doc.add(new TextField("fileN", "英文", Store.YES));
		doc.add(new TextField("fileC", "English", Store.YES));
		indexWriter.updateDocument(new Term("fileName", "中"), doc);
		indexWriter.close();

	}

	/**
	 * 
	 * @throws Exception 
	 * @Title: otherSearch @Description: 高级查询 @param 设定文件 @return void
	 *         返回类型 @throws
	 */
	@Test
	public void mathAll() throws Exception {

		IndexSearcher indexSearcher = getIndexSearcher();
		Query query = new MatchAllDocsQuery();
		printableResult(indexSearcher, query);
		indexSearcher.getIndexReader().close();
	}
	/**
	 * @throws Exception 
	 * @throws IOException 
	 * 
	* @Title: booleanSearch  
	* @Description: 组合条件查询
	* @param     设定文件  
	* @return void    返回类型  
	* @throws
	 */
	@Test
	public void booleanSearch() throws Exception{
		IndexSearcher indexSearcher = getIndexSearcher();
		BooleanQuery booleanQuery = new BooleanQuery();
		Query query1 = new TermQuery(new Term("fileName", "中间件"));
		Query query2 = new TermQuery(new Term("fileContent", "中间件"));
		booleanQuery.add(query1, Occur.MUST);
		booleanQuery.add(query2, Occur.MUST);
		printableResult(indexSearcher, booleanQuery);
		indexSearcher.getIndexReader().close();
	}
	/**
	 * 
	* @Title: queryParser  
	* @Description: 语意查询
	* @param @throws Exception    设定文件  
	* @return void    返回类型  
	* @throws
	 */
	@Test
	public void queryParser() throws Exception {
		IndexSearcher indexSearcher = getIndexSearcher();
		QueryParser queryParser = new QueryParser("fileName", new IKAnalyzer());// 执行查询的解析器,并指定默认的查询域为：fileName
		Query query = queryParser.parse("中间件");
		printableResult(indexSearcher, query);
		indexSearcher.getIndexReader().close();
	}
	/**
	 * 
	* @Title: queryParserMultiDefaultFields  
	* @Description: 多默认域名的语意查询
	* @param @throws Exception    设定文件  
	* @return void    返回类型  
	* @throws
	 */
	@Test
	public void multiFieldQueryParse() throws Exception {
		IndexSearcher indexSearcher = getIndexSearcher();
		// 默认域数组
		String[] fields = {"fileName","fileContent"};
		QueryParser queryParser = new MultiFieldQueryParser(fields, new IKAnalyzer());
		Query query = queryParser.parse("lucene");
		printableResult(indexSearcher, query);
		indexSearcher.getIndexReader().close();
	}
}
