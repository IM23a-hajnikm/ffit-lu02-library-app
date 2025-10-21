package ch.bzz;

import ch.bzz.db.BookPersistor;
import ch.bzz.model.Book;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class JavalinMain {

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            // default config is fine
        }).start(7070);

        app.get("/books", JavalinMain::handleListBooks);
    }

    private static void handleListBooks(Context ctx) {
        String limitParam = ctx.queryParam("limit");
        Integer limit = null;
        if (limitParam != null && !limitParam.isBlank()) {
            try {
                limit = Integer.parseInt(limitParam);
            } catch (NumberFormatException ignored) {
                // ignore invalid limit; behave like console version and just return default list
            }
        }

        var persistor = new BookPersistor();
        List<Book> books = persistor.findAll();

        int max = (limit == null) ? books.size() : Math.max(0, Math.min(limit, books.size()));
        if (max != books.size()) {
            books = books.subList(0, max);
        }

        ctx.json(books);
    }
}



