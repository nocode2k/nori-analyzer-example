import java.nio.file.Paths;

import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.analysis.ko.KoreanPartOfSpeechStopFilter;
import org.apache.lucene.analysis.ko.KoreanTokenizer;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.queryparser.classic.QueryParser;

public class LuceneSample {
    public static void main(String[] args) throws Exception {
        write();
        search();
    }

    public static void write() throws Exception {
        // nori analyzer초기화
        Analyzer analyzer = getAnalyzer();
        // 인덱스 디렉토리 설정
        Directory dir = new MMapDirectory(Paths.get("index"));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(dir, config);

        // title 및 content 필드가 있는 문서를 인덱스에 추가
        Document doc = new Document();
        doc.add(new Field("title", "뉴스제목 - 1 -", termVector()));
        doc.add(new Field("content", "검색엔진은 당신이 무슨 생각하는지 안다", termVector()));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new Field("title", "뉴스제목 - 2 -", termVector()));
        doc.add(new Field("content", "나는 당신이 무슨 생각하는지 안다", termVector()));
        writer.addDocument(doc);

        writer.close();
    }

    public static void search() throws Exception {
        // nori analyzer초기화
        Analyzer analyzer = getAnalyzer();
        // 검색 준비
        IndexReader reader = DirectoryReader.open(MMapDirectory.open(Paths.get("index")));
        IndexSearcher searcher = new IndexSearcher(reader);

        // 검색어 준비
        QueryParser parser = new QueryParser("content", analyzer);
        Query query = parser.parse("content:검색");

        // 검색 수행
        TopScoreDocCollector collector = TopScoreDocCollector.create(100, 100);
        searcher.search(query, collector);

        // 검색 결과(상위 10건) 출력
        ScoreDoc[] hits = collector.topDocs(0, 10).scoreDocs;
        for (ScoreDoc hit : hits) {
            Document doc = searcher.doc(hit.doc);
            System.out.println(doc.get("title") + " | " + doc.get("content"));
        }
    }

    private static Analyzer getAnalyzer() {
        return new KoreanAnalyzer(
                null,
                KoreanTokenizer.DecompoundMode.DISCARD,
                KoreanPartOfSpeechStopFilter.DEFAULT_STOP_TAGS,
                false);
    }

    private static FieldType termVector() {
        FieldType tvType = new FieldType();
        tvType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        tvType.setStored(true);
        tvType.setStoreTermVectors(true);
        tvType.setStoreTermVectorOffsets(true);
        tvType.setStoreTermVectorPositions(true);
        tvType.setStoreTermVectorPayloads(true);
        return tvType;
    }
}
