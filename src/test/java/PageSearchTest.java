import model.Page;
import model.PageSearch;
import model.PageSearchResult;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoringRewrite;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PageSearchTest {
    private PageSearch search;
    @BeforeEach
    void setUp() throws IOException {
        List<Page> pageList = new ArrayList<>();
        pageList.add(new Page("Page 1", "I am a page\nA small page, and " +
                "a page with two paragraphs\n\nThis is a new paragraph. " +
                "Awesome.",
                false));
        pageList.add(new Page("Page 2", "I am page 2\n\nI have 3 " +
                "paragraphs\n\nSo I am long. Nice.",false));
        pageList.add(new Page("Page 3", "I am page 3, I only have one " +
                "paragraph. I am also private, but this should not matter.",
                true));
        search = new PageSearch(pageList);
    }


    void correctNumSearchTest(String query, int resultLength) throws ParseException,
            IOException {
        List<PageSearchResult> results = search.search(query);
        int length = 0;
        for (PageSearchResult result: results){
            length++;
        }
        assertEquals(resultLength,length, String.format("expected %d results," +
                " got %d", resultLength, length));

    }
    @Test
    void searchTest() throws ParseException, IOException {
        List<PageSearchResult> results = search.search("paragraph*");
        correctNumSearchTest("paragraph*",4);
        String[] titles = new String[4];
        String[] contents = new String[4];
        int count = 0;
        for (PageSearchResult result: results){
            String[] splits = result.getFormattedContent().split("\n\n");
            titles[count] = splits[0];
            contents[count] = splits[1];
            count++;
        }
        Arrays.sort(titles);
        Arrays.sort(contents);
        String[] correctTitles = {"Page 1", "Page 2", "Page 3", "Page 1"};
        Arrays.sort(correctTitles);
        String[] correctContents = {"I am a page\nA small page, and " +
                "a page with two paragraphs", "This is a new paragraph. Awesome.", "I" +
                " have 3 paragraphs", "I am page 3, I only have one paragraph. I am also private, but this should not matter."};
        Arrays.sort(correctContents);
        //checks that all of the correct page titles, and paragraphs are
        // returned, and checks that the number of results is correct.
        assertAll(
                () -> correctNumSearchTest("paragraph*",4),
                () ->assertEquals(Arrays.toString(correctTitles),
                        Arrays.toString(titles)
                , String.format("Expected %s , got %s",
                Arrays.toString(correctTitles), Arrays.toString(titles))),
        () -> assertEquals(Arrays.toString(correctContents),
                Arrays.toString(contents),String.format("Expected %s , got %s",
                Arrays.toString(correctContents), Arrays.toString(contents))));
    }
    @Test
    void noResultTest() throws ParseException, IOException {
        List<PageSearchResult> nullResult = search.search(
                "supercalifragilisticexpialidocious");
        int nullLength = 0;
        for (PageSearchResult result: nullResult){
            nullLength++;
        }
        assertEquals(0,nullLength, String.format("Expected length of search " +
                        "for supercalifragilisticexpialidocious to be 0, got %d",
                nullLength));
    }
}
