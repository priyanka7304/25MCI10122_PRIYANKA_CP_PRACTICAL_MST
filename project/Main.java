import java.util.Scanner;

/**
 * ╔══════════════════════════════════════════════════════════╗
 *  Library Management System
 *  Data Structures: HashMap | Queue | Stack
 * ╚══════════════════════════════════════════════════════════╝
 */
public class Main {

    public static void main(String[] args) {
        LibrarySystem library = new LibrarySystem();
        System.out.println(banner());

        // ── Pre-load catalog ────────────────────────────────────────────────
        library.addBook("ISBN001", "Clean Code",                    "Robert C. Martin");
        library.addBook("ISBN002", "The Pragmatic Programmer",      "Andrew Hunt");
        library.addBook("ISBN003", "Introduction to Algorithms",    "Cormen et al.");
        library.addBook("ISBN004", "Design Patterns",               "Gang of Four");
        library.addBook("ISBN005", "You Don't Know JS",             "Kyle Simpson");

        // ── Demo scenario ───────────────────────────────────────────────────
        runDemo(library);

        // ── Interactive menu ────────────────────────────────────────────────
        interactiveMenu(library);
    }

    // ── Demo: showcases all three data structures in action ──────────────────
    private static void runDemo(LibrarySystem library) {
        System.out.println("\n══════════════════ DEMO SCENARIO ══════════════════");

        System.out.println("\n▶ Alice issues 'Clean Code'");
        library.issueBook("ISBN001", "Alice");

        System.out.println("\n▶ Bob tries to issue the same book (goes to queue)");
        library.issueBook("ISBN001", "Bob");

        System.out.println("\n▶ Charlie also joins the queue");
        library.issueBook("ISBN001", "Charlie");

        System.out.println("\n▶ Dave issues 'Design Patterns'");
        library.issueBook("ISBN004", "Dave");

        library.displayWaitingQueue("ISBN001");
        library.displayIssuedBooks();

        System.out.println("\n▶ Alice returns 'Clean Code' → Bob auto-issued (FIFO)");
        library.returnBook("ISBN001", "Alice");

        library.displayWaitingQueue("ISBN001");

        System.out.println("\n▶ Bob returns 'Clean Code' → Charlie auto-issued");
        library.returnBook("ISBN001", "Bob");

        library.displayTransactionHistory();

        System.out.println("\n════════════════ END OF DEMO ════════════════════\n");
    }

    // ── Interactive CLI menu ──────────────────────────────────────────────────
    private static void interactiveMenu(LibrarySystem library) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            printMenu();
            System.out.print("  Enter choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("  ISBN   : "); String isbn   = sc.nextLine().trim();
                    System.out.print("  Title  : "); String title  = sc.nextLine().trim();
                    System.out.print("  Author : "); String author = sc.nextLine().trim();
                    library.addBook(isbn, title, author);
                }
                case "2" -> {
                    System.out.print("  ISBN   : "); String isbn   = sc.nextLine().trim();
                    System.out.print("  Member : "); String member = sc.nextLine().trim();
                    library.issueBook(isbn, member);
                }
                case "3" -> {
                    System.out.print("  ISBN   : "); String isbn   = sc.nextLine().trim();
                    System.out.print("  Member : "); String member = sc.nextLine().trim();
                    library.returnBook(isbn, member);
                }
                case "4" -> library.displayAllBooks();
                case "5" -> library.displayAvailableBooks();
                case "6" -> library.displayIssuedBooks();
                case "7" -> {
                    System.out.print("  ISBN   : "); String isbn = sc.nextLine().trim();
                    library.displayWaitingQueue(isbn);
                }
                case "8" -> library.displayAllWaitingQueues();
                case "9" -> library.displayTransactionHistory();
                case "10" -> {
                    System.out.print("  ISBN   : "); String isbn = sc.nextLine().trim();
                    library.searchBook(isbn);
                }
                case "11" -> {
                    System.out.print("  ISBN   : "); String isbn = sc.nextLine().trim();
                    library.removeBook(isbn);
                }
                case "0" -> {
                    System.out.println("\n  Goodbye! 📚\n");
                    sc.close();
                    return;
                }
                default -> System.out.println("  ⚠  Invalid option. Try again.");
            }
            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("""
                  ┌──────────────────────────────────────┐
                  │           LIBRARY MENU               │
                  ├──────────────────────────────────────┤
                  │  1.  Add Book to Catalog             │
                  │  2.  Issue Book                      │
                  │  3.  Return Book                     │
                  │  4.  View All Books                  │
                  │  5.  View Available Books            │
                  │  6.  View Issued Books               │
                  │  7.  View Waiting Queue (by ISBN)    │
                  │  8.  View All Waiting Queues         │
                  │  9.  Transaction History (Stack)     │
                  │  10. Search Book by ISBN             │
                  │  11. Remove Book from Catalog        │
                  │  0.  Exit                            │
                  └──────────────────────────────────────┘""");
    }

    private static String banner() {
        return """
                ╔══════════════════════════════════════════════════════════════╗
                ║          📚  LIBRARY MANAGEMENT SYSTEM  📚                  ║
                ╠══════════════════════════════════════════════════════════════╣
                ║  Data Structures:                                            ║
                ║    • HashMap  → O(1) Book Lookup by ISBN                     ║
                ║    • Queue    → Fair Waiting List per Book (FIFO)            ║
                ║    • Stack    → Transaction History (LIFO / Audit Trail)     ║
                ╚══════════════════════════════════════════════════════════════╝
                """;
    }
}
