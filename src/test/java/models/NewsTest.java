package models;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class NewsTest {


    private News simpleNews(){
        return new News( "Simple News", "Currently we have nothing to report");
    }

    private News altNews(){
        return new News("Food Prices","Kenyans demand food prices to be lowered");
    }

    @Test
    public void initializeCorrectly(){
        News news = altNews();
        assertTrue(news instanceof News);
    }

    @Test
    public void getFunctionsWorkCorrectly(){
        News news = simpleNews();
        assertEquals(news.getTitle(),simpleNews().getTitle());
        assertEquals(news.getDescription(),simpleNews().getDescription());
        assertEquals(news.getType(),simpleNews().getType());
    }

    @Test
    public void getDifferentNewsType(){
        News news = simpleNews();
        News news2 = altNews();
        news2.setType("Current affairs");
        news2.setAuthor("Star");
        assertEquals(news.getType(),"General");
        assertEquals(news2.getType(),"Current affairs");
        assertEquals(news2.getAuthor(),"Star");
    }


}