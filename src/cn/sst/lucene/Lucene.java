package cn.sst.lucene;

import java.io.File;
import java.io.IOException;


import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleDocValuesField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * 
 * @ClassName: Lucene 
 * @Description: 索引库创建
 * @author: sunshengteng
 * @date: 2018年3月5日 下午5:53:08
 */
public class Lucene {


	/**
	 * @throws IOException 
	 * 
	* @Title: createIndex  
	* @Description: 创建索引库
	* @param     设定文件  
	* @return void    返回类型  
	* @throws
	 */
	@Test
	public void createIndex() throws IOException{
		// 1、创建一个indexWriter对象
		//     指定一个索引库的位置
		//     指定一个分析器，对文档内容进行分析
		File fileDir = new File("/Users/sunshengteng/Java_project/Workspaces/Lucene/temp/index");
		Directory dir = FSDirectory.open(fileDir);
//		Directory ramDir = new RAMDirectory(); 指定索引库的位置是内存
		Analyzer iKAnalyzer = new IKAnalyzer();
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST, iKAnalyzer);
		IndexWriter indexWriter = new IndexWriter(dir, indexWriterConfig);
		
		File file = new File("/Users/sunshengteng/Java_project/Workspaces/Lucene/files");
		File[] files = file.listFiles();
		for (File childFile : files) {
			// 2、创建文档对象
			Document document = new Document();
			// 3、创建Field对象，并将Field对象添加到Document
			//文件名
			String file_name = childFile.getName();
			Field fileNameField = new TextField("fileName", file_name, Store.YES);
		    // 文件路径
			String file_path = childFile.getPath();
			Field filePathField = new StoredField("filePath", file_path);
			//文件大小
			long file_size = FileUtils.sizeOf(childFile);
			Field fileSizeField = new DoubleDocValuesField("fileSize", file_size);
			// 获取文件内容
			String file_content = FileUtils.readFileToString(childFile, "UTF-8");
			Field fileContentField = new TextField("fileContent", file_content, Store.YES);
			document.add(fileNameField);
			document.add(filePathField);
			document.add(fileSizeField);
			document.add(fileContentField);
			// 4、使用indexWriter将文档和索引一起写入索引库
			indexWriter.addDocument(document);
		}
		// 5、关闭资源
		indexWriter.close();
	}
	/**
	 * 
	* @Title: searchIndex  
	* @Description: 查询索引库
	* @param @throws IOException    设定文件  
	* @return void    返回类型  
	* @throws
	 */
	@Test
	public void searchIndex() throws IOException{
		// 1、指定索引库位置
		File file = new File("/Users/sunshengteng/Java_project/Workspaces/Lucene/temp/index");
		Directory indexDir = FSDirectory.open(file);
		// 2、创建indexReader对象(操作索引库的流对象)
		IndexReader indexReader = DirectoryReader.open(indexDir);
		// 3、创建indexSearcher对象（搜索对象）
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		// 4、创建query
		Query query = new TermQuery(new Term("fileN", "英"));
		// 5、执行查询
		TopDocs topDocs = indexSearcher.search(query, 2);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;// 获取评分后的文档
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docId = scoreDoc.doc;//获取文档的ID
			Document document = indexSearcher.doc(docId);//根据文档ID查询文档
			// 6、渲染结果
			System.out.println(document.get("fileName"));
			System.out.println(document.get("filePath"));
			System.out.println(document.get("fileSize"));
			System.out.println(document.get("fileContent"));
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
			
		}
		// 7、关闭流
		indexReader.close();
	}
	
}
