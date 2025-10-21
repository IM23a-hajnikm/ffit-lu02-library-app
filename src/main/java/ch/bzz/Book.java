package ch.bzz;

public class Book {

	private final int id;
	private final String isbn;
	private final String title;
	private final String author;
	private final int year;

	public static final Book BOOK_1 = new Book(
			1,
			"978-3-8362-9544-4",
			"Java ist auch eine Insel",
			"Christian Ullenboom",
			2023
	);

	public static final Book BOOK_2 = new Book(
			2,
			"978-3-658-43573-8",
			"Grundkurs Java",
			"Dietmar Abts",
			2024
	);

	public Book(int id, String isbn, String title, String author, int year) {
		this.id = id;
		this.isbn = isbn;
		this.title = title;
		this.author = author;
		this.year = year;
	}

	public int getId() {
		return id;
	}

	public String getIsbn() {
		return isbn;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public int getYear() {
		return year;
	}
}




