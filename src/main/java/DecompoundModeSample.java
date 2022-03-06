import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.analysis.ko.KoreanPartOfSpeechStopFilter;
import org.apache.lucene.analysis.ko.KoreanTokenizer;
import org.apache.lucene.analysis.ko.tokenattributes.PartOfSpeechAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

public class DecompoundModeSample {
    public static void main(String[] args) throws Exception {

        String text = "백두산이 마르고 닳도록 하느님이 보우하사 우리나라 만세";

        Analyzer analyzer =
                new KoreanAnalyzer(
                        null,
                        KoreanTokenizer.DecompoundMode.NONE,
                        KoreanPartOfSpeechStopFilter.DEFAULT_STOP_TAGS,
                        false);
        //NONE : 어근을 분리하지 않고 완성된 합성어만 저장합니다.
        displayToken(analyzer, text, "NONE");

        analyzer =
                new KoreanAnalyzer(
                        null,
                        KoreanTokenizer.DecompoundMode.DISCARD,
                        KoreanPartOfSpeechStopFilter.DEFAULT_STOP_TAGS,
                        false);
        //DISCARD (디폴트) : 합성어를 분리하여 각 어근만 저장합니다.
        displayToken(analyzer, text, "DISCARD");

        analyzer =
                new KoreanAnalyzer(
                        null,
                        KoreanTokenizer.DecompoundMode.MIXED,
                        KoreanPartOfSpeechStopFilter.DEFAULT_STOP_TAGS,
                        false);
        //MIXED : 어근과 합성어를 모두 저장합니다.
        displayToken(analyzer, text, "MIXED");
    }

    public static void displayToken(Analyzer analyzer, String text, String mode) throws IOException {
        try (TokenStream ts = analyzer.tokenStream("dummy", text)) {
            StringBuilder resultBuffer = new StringBuilder();
            CharTermAttribute termAtt = ts.getAttribute(CharTermAttribute.class);
            PartOfSpeechAttribute partOfSpeechAttribute = ts.getAttribute(PartOfSpeechAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                resultBuffer.append(termAtt);
                resultBuffer.append(" type=").append(partOfSpeechAttribute.getPOSType());
                resultBuffer.append(" tag=").append(partOfSpeechAttribute.getLeftPOS());
                resultBuffer.append('\n');
            }
            ts.end();
            System.out.println("================= "+mode+" =================");
            System.out.println(resultBuffer.toString());
        }
    }
}
