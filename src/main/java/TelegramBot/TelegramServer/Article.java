package TelegramBot.TelegramServer;

import java.util.List;

public class Article{
    public String id;
    public String title;
    public String link;
    public String published;
    public List<SubArticle> sub_articles;
    public Source source;
}