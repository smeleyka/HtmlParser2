import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by smeleyka on 18.07.17.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        String footer = "</tbody></table>\n" +
                "<script src=\"https://code.jquery.com/jquery-3.2.1.min.js\"\n" +
                "integrity=\"sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4=\"\n" +
                "crossorigin=\"anonymous\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn.datatables.net/v/dt/dt-1.10.15/datatables.min.js\"></script>\n" +
                "<script type=\"text/javascript\" class=\"init\">\n" +
                "$(document).ready(function() {\n" +
                "$('#test_table').DataTable();\n" +
                "} );\n" +
                "</script>\n"+
                "</body>\n" +
                "</html>";

        String head = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdn.datatables.net/v/dt/dt-1.10.15/datatables.min.css\"/>\n" +

                "</head>\n" +
                "<body>\n" +
                "<table id=\"test_table\">\n" +
                "<thead><tr><th>Имя</th><th>Стоимость</th><th>Картинка</th></tr></thead><tbody>\n";


        ArrayList<Article> articleList = new ArrayList<>();
        String domain = "https://www.avito.ru";
        String mainQuery = "https://www.avito.ru/vyborg/tovary_dlya_kompyutera/monitory?q=";
        String query = null;
        Scanner sc = new Scanner(System.in);
        int pages_number;
        System.out.printf("Введите запрос: ");
        query = URLEncoder.encode(sc.nextLine(), "UTF-8");

        FileOutputStream file = new FileOutputStream("test.html");
        OutputStreamWriter fileWriter = new OutputStreamWriter(file);


        Document doc = Jsoup.connect("https://www.avito.ru/sankt-peterburg/tovary_dlya_kompyutera/komplektuyuschie?q=" + query).get();
        Elements page = doc.getElementsByAttributeValue("class", "pagination-pages clearfix");
        pages_number = page.get(0).children().size() - 1;

        fileWriter.write(head);

        for (int i = 0; i < pages_number; i++) {

            doc = Jsoup.connect("https://www.avito.ru/sankt-peterburg/tovary_dlya_kompyutera/komplektuyuschie?q=" + query + "&p=" + (i + 1)).get();
            Elements h2Elements = doc.getElementsByAttributeValue("class", "item_table-header");
            h2Elements.forEach(h2Element -> {

                System.out.println(page.get(0).children().size());
                String img = null;
                String raw_img = h2Element.parent().parent().getElementsByClass("photo-count-show").attr("src");
                if (raw_img.isEmpty())
                    raw_img = "//vignette3.wikia.nocookie.net/lego/images/a/ac/No-Image-Basic.png/revision/latest?cb=20130819001030";
                img = "https:" + raw_img;


                String name = h2Element.getElementsByAttribute("href").attr("title");
                String url = domain + h2Element.getElementsByAttribute("href").attr("href");
                String cost = h2Element.child(1).text().replaceAll("( |\\D)*", "");
                System.out.println(cost);


                articleList.add(new Article(url, name, cost, img));
            });


            articleList.forEach(article -> {
                try {
                    fileWriter.write(article.body());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        fileWriter.write(footer);
        fileWriter.close();
        file.close();

    }
}

class Article {

    private String url;
    private String name;
    //private int cost;
    private String cost;
    private String img;
    private static String body;
    private static String head;
    private static String footer;



    public Article(String url, String name, String cost, String img) {
        this.url = url;
        this.name = name;
        this.cost = cost;
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Article{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", cost='" + cost + '\'' +
                '}';
    }

    public String body() {
        String body;
        body = "<tr>\n" +
                "<td><div><a href=\"" + url + "\">" + name + "</a></div></td>\n" +
                "<td><div>" + cost + "</div></td>\n" +
                "<td><div><img width=\"100px\" src=\"" + img + "\"></div></td>\n" +
                "</tr>\n";
        return body;
    }

    public String footer(){

        return footer;
    }

    public String head(){

        return head;
    }

}
