import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.analysis.ko.KoreanPartOfSpeechStopFilter;
import org.apache.lucene.analysis.ko.KoreanTokenizer;
import org.apache.lucene.analysis.ko.tokenattributes.PartOfSpeechAttribute;
import org.apache.lucene.analysis.ko.tokenattributes.ReadingAttribute;
import org.apache.lucene.analysis.tokenattributes.*;
import java.io.IOException;

public class SimpleTokenizerSample {
    public static void main(String[] args) throws Exception {
        Analyzer analyzer =
                new KoreanAnalyzer(
                        null,
                        KoreanTokenizer.DecompoundMode.DISCARD,
                        KoreanPartOfSpeechStopFilter.DEFAULT_STOP_TAGS,
                        false);

        System.out.println("=============================================================");
        System.out.println(getKoreanAnalysisResult(analyzer, "喜悲哀歡"));
        System.out.println("=============================================================");
    }


    public static String getKoreanAnalysisResult(Analyzer analyzer, String text) throws IOException {
        try (TokenStream ts = analyzer.tokenStream("dummy", text)) {
            StringBuilder resultBuffer = new StringBuilder();
            CharTermAttribute termAtt = ts.getAttribute(CharTermAttribute.class);
            PositionIncrementAttribute posIncAtt = ts.getAttribute(PositionIncrementAttribute.class);
            PositionLengthAttribute posLengthAtt = ts.getAttribute(PositionLengthAttribute.class);
            PartOfSpeechAttribute partOfSpeechAttribute = ts.getAttribute(PartOfSpeechAttribute.class);
            ReadingAttribute readingAttribute = ts.getAttribute(ReadingAttribute.class);
            OffsetAttribute offsetAtt = ts.getAttribute(OffsetAttribute.class);

            ts.reset();

            int pos = -1;
            while (ts.incrementToken()) {
                pos += posIncAtt.getPositionIncrement();
                resultBuffer.append(termAtt);
                resultBuffer.append(" at pos=");
                resultBuffer.append(pos);
                if (readingAttribute != null) {
                    System.out.println(readingAttribute.getReading());
                }
                if (posLengthAtt != null) {
                    resultBuffer.append(" to pos=");
                    resultBuffer.append(pos + posLengthAtt.getPositionLength());
                }
                resultBuffer.append(" offsets=");
                resultBuffer.append(offsetAtt.startOffset());
                resultBuffer.append('-');
                resultBuffer.append(offsetAtt.endOffset());
                resultBuffer.append(" type=").append(partOfSpeechAttribute.getPOSType());
                resultBuffer.append(" tag=").append(partOfSpeechAttribute.getLeftPOS());
                resultBuffer.append('\n');
            }
            ts.end();
            return resultBuffer.toString();
        }
    }
}
