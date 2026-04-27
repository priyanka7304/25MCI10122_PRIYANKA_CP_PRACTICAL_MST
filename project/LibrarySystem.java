import java.util.*;

/**
 * LibrarySystem — Core engine of the Library Management System
 *
 * Data Structures Used:
 *  - HashMap<String, Book>              : O(1) book lookup by ISBN
 *  - HashMap<String, Queue<String>>     : Waiting queues per ISBN (FIFO)
 *  - HashMap<String, String>            : Tracks which member holds which book
 *  - Stack<Transaction>                 : Full transaction history (LIFO for undo/audit)
 */
public class LibrarySystem {

    // ── Data Structures ────────────────────────────────────────────────────────

    /** Master catalog: ISBN → Book */
    private final HashMap<String, Book> bookCatalog = new HashMap<>();

    /** Waiting queue per book: ISBN → Queue of member names (FIFO) */
    private final HashMap<String, Queue<String>> waitingQueues = new HashMap<>();

    /** Currently issued: ISBN → Member Name */
    private final HashMap<String, String> issuedBooks = new HashMap<>();

    /** Transaction history stack (most recent on top) */
    private final Stack<Transaction> transactionHistory = new Stack<>();

    private int transactionCounter = 1;

    // ── Book Catalog Management ─────────────────────────────────────────────────

    public void addBook(String isbn, String title, String author) {
        if (bookCatalog.containsKey(isbn)) {
            System.out.println("  ⚠  Book with ISBN " + isbn + " already exists.");
            return;
        }
        bookCatalog.put(isbn, new Book(isbn, title, author));
        waitingQueues.put(isbn, new LinkedList<>());
        System.out.println("  ✔  Added: " + title + " [" + isbn + "]");
    }

    public void removeBook(String isbn) {
        if (!bookCatalog.containsKey(isbn)) {
            System.out.println("  ✘  Book not found: " + isbn);
            return;
        }
        if (issuedBooks.containsKey(isbn)) {
            System.out.println("  ⚠  Cannot remove — book is currently issued to: "
                    + issuedBooks.get(isbn));
            return;
        }
        Book book = bookCatalog.remove(isbn);
        waitingQueues.remove(isbn);
        System.out.println("  ✔  Removed: " + book.getTitle());
    }

    // ── Issue Book ──────────────────────────────────────────────────────────────

    /**
     * Issues a book to a member.
     * If unavailable, the member is added to the waiting queue for that book.
     */
    public void issueBook(String isbn, String memberName) {
        System.out.println();
        if (!bookCatalog.containsKey(isbn)) {
            System.out.println("  ✘  Book not found in catalog: " + isbn);
            return;
        }

        Book book = bookCatalog.get(isbn);

        if (book.isAvailable()) {
            // Issue the book
            book.setAvailable(false);
            issuedBooks.put(isbn, memberName);

            Transaction txn = new Transaction(
                    String.format("T%04d", transactionCounter++),
                    memberName, isbn, book.getTitle(), "ISSUE"
            );
            transactionHistory.push(txn);

            System.out.println("  ✔  ISSUED  → \"" + book.getTitle()
                    + "\" to " + memberName);
            System.out.println("     " + txn);
        } else {
            // Book unavailable — join waiting queue
            Queue<String> queue = waitingQueues.get(isbn);
            if (queue.contains(memberName)) {
                System.out.println("  ⚠  " + memberName
                        + " is already in the waiting queue for this book.");
            } else {
                queue.offer(memberName);
                System.out.println("  ℹ  Book unavailable. \"" + memberName
                        + "\" added to waiting queue. Position: " + queue.size());
                System.out.println("     Currently held by: " + issuedBooks.get(isbn));
            }
        }
    }

    // ── Return Book ─────────────────────────────────────────────────────────────

    /**
     * Returns a book.
     * If there's a waiting queue, the book is auto-issued to the next person.
     */
    public void returnBook(String isbn, String memberName) {
        System.out.println();
        if (!bookCatalog.containsKey(isbn)) {
            System.out.println("  ✘  Book not found: " + isbn);
            return;
        }

        if (!issuedBooks.containsKey(isbn)) {
            System.out.println("  ⚠  This book was not issued to anyone.");
            return;
        }

        String currentHolder = issuedBooks.get(isbn);
        if (!currentHolder.equalsIgnoreCase(memberName)) {
            System.out.println("  ✘  Return failed. Book is issued to '"
                    + currentHolder + "', not '" + memberName + "'.");
            return;
        }

        Book book = bookCatalog.get(isbn);
        issuedBooks.remove(isbn);
        book.setAvailable(true);

        Transaction returnTxn = new Transaction(
                String.format("T%04d", transactionCounter++),
                memberName, isbn, book.getTitle(), "RETURN"
        );
        transactionHistory.push(returnTxn);

        System.out.println("  ✔  RETURNED → \"" + book.getTitle()
                + "\" by " + memberName);
        System.out.println("     " + returnTxn);

        // Check waiting queue
        Queue<String> queue = waitingQueues.get(isbn);
        if (!queue.isEmpty()) {
            String nextMember = queue.poll(); // dequeue next (FIFO)
            System.out.println();
            System.out.println("  ◎  Waiting queue active — auto-issuing to next member...");
            issueBook(isbn, nextMember);
        }
    }

    // ── Display Methods ─────────────────────────────────────────────────────────

    public void displayAllBooks() {
        System.out.println();
        System.out.println("  ┌─────────────────────────────────────────────────────────────┐");
        System.out.println("  │                      BOOK CATALOG                           │");
        System.out.println("  └─────────────────────────────────────────────────────────────┘");
        if (bookCatalog.isEmpty()) {
            System.out.println("  No books in catalog.");
            return;
        }
        for (Book book : bookCatalog.values()) {
            System.out.println("  " + book);
        }
    }

    public void displayAvailableBooks() {
        System.out.println();
        System.out.println("  ┌─────────────────────────────────────────────────────────────┐");
        System.out.println("  │                    AVAILABLE BOOKS                          │");
        System.out.println("  └─────────────────────────────────────────────────────────────┘");
        boolean any = false;
        for (Book book : bookCatalog.values()) {
            if (book.isAvailable()) {
                System.out.println("  " + book);
                any = true;
            }
        }
        if (!any) System.out.println("  No books currently available.");
    }

    public void displayIssuedBooks() {
        System.out.println();
        System.out.println("  ┌─────────────────────────────────────────────────────────────┐");
        System.out.println("  │                     ISSUED BOOKS                            │");
        System.out.println("  └─────────────────────────────────────────────────────────────┘");
        if (issuedBooks.isEmpty()) {
            System.out.println("  No books currently issued.");
            return;
        }
        for (Map.Entry<String, String> entry : issuedBooks.entrySet()) {
            Book book = bookCatalog.get(entry.getKey());
            System.out.printf("  %-12s | %-30s | Held by: %s%n",
                    entry.getKey(), book.getTitle(), entry.getValue());
        }
    }

    public void displayWaitingQueue(String isbn) {
        System.out.println();
        if (!bookCatalog.containsKey(isbn)) {
            System.out.println("  ✘  Book not found: " + isbn);
            return;
        }
        Queue<String> queue = waitingQueues.get(isbn);
        Book book = bookCatalog.get(isbn);
        System.out.println("  ┌─────────────────────────────────────────────────────────────┐");
        System.out.println("  │  Waiting Queue for: " + book.getTitle());
        System.out.println("  └─────────────────────────────────────────────────────────────┘");
        if (queue.isEmpty()) {
            System.out.println("  No one in the waiting queue.");
        } else {
            int pos = 1;
            for (String member : queue) {
                System.out.println("  " + pos++ + ". " + member);
            }
        }
    }

    public void displayAllWaitingQueues() {
        System.out.println();
        System.out.println("  ┌─────────────────────────────────────────────────────────────┐");
        System.out.println("  │                   ALL WAITING QUEUES                        │");
        System.out.println("  └─────────────────────────────────────────────────────────────┘");
        boolean any = false;
        for (Map.Entry<String, Queue<String>> entry : waitingQueues.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                Book book = bookCatalog.get(entry.getKey());
                System.out.println("  \"" + book.getTitle() + "\" → " + entry.getValue());
                any = true;
            }
        }
        if (!any) System.out.println("  No active waiting queues.");
    }

    public void displayTransactionHistory() {
        System.out.println();
        System.out.println("  ┌─────────────────────────────────────────────────────────────┐");
        System.out.println("  │              TRANSACTION HISTORY (Latest First)             │");
        System.out.println("  └─────────────────────────────────────────────────────────────┘");
        if (transactionHistory.isEmpty()) {
            System.out.println("  No transactions recorded.");
            return;
        }
        // Iterate stack without popping (top = most recent)
        List<Transaction> copy = new ArrayList<>(transactionHistory);
        for (int i = copy.size() - 1; i >= 0; i--) {
            System.out.println("  " + copy.get(i));
        }
    }

    public void searchBook(String isbn) {
        System.out.println();
        Book book = bookCatalog.get(isbn); // O(1) HashMap lookup
        if (book == null) {
            System.out.println("  ✘  No book found with ISBN: " + isbn);
        } else {
            System.out.println("  ✔  Found: " + book);
            if (!book.isAvailable()) {
                System.out.println("     Held by: " + issuedBooks.get(isbn));
                Queue<String> q = waitingQueues.get(isbn);
                System.out.println("     Queue size: " + q.size());
            }
        }
    }
}
