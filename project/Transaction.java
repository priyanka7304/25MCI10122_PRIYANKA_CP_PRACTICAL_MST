import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String transactionId;
    private String memberName;
    private String bookIsbn;
    private String bookTitle;
    private LocalDateTime timestamp;
    private String type; // "ISSUE" or "RETURN"

    public Transaction(String transactionId, String memberName, String bookIsbn,
                       String bookTitle, String type) {
        this.transactionId = transactionId;
        this.memberName = memberName;
        this.bookIsbn = bookIsbn;
        this.bookTitle = bookTitle;
        this.timestamp = LocalDateTime.now();
        this.type = type;
    }

    public String getTransactionId() { return transactionId; }
    public String getMemberName()    { return memberName; }
    public String getBookIsbn()      { return bookIsbn; }
    public String getBookTitle()     { return bookTitle; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getType()          { return type; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] TXN#%s | Member: %-15s | Book: %-30s | ISBN: %s",
                type, transactionId, memberName, bookTitle, bookIsbn);
    }
}
