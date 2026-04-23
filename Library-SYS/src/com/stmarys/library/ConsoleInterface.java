package com.stmarys.library;

import java.util.Scanner;
import java.util.List;
import java.sql.SQLException;

public class ConsoleInterface {
    private final BookDAO bookDAO = new BookDAO();
    private final MemberDAO memberDAO = new MemberDAO();
    private final BorrowDAO borrowDAO = new BorrowDAO();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        boolean exit = false;
        while (!exit) {
            printMainMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1": manageBooks(); break;
                case "2": manageMembers(); break;
                case "3": manageBorrowing(); break;
                case "4": searchRecords(); break;
                case "5": 
                    System.out.println("\nSystem Message: Exiting System. Goodbye!");
                    exit = true; 
                    break;
                default: 
                    System.out.println("Invalid option, try again.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\nSt Mary's University, Twickenham");
        System.out.println("      STAFF MANAGEMENT CONSOLE      ");
        System.out.println("Welcome Staff!");
        System.out.println("1. Manage Books");
        System.out.println("2. Manage Members");
        System.out.println("3. Manage Borrowing Records");
        System.out.println("4. Search Records");
        System.out.println("5. Exit System");
        System.out.print("Select an option: ");
    }

    private void manageBooks() {
        System.out.println("\nBOOK MANAGEMENT");
        System.out.println("1. View All Books\n2. Add Book\n3. Update Status\n4. Delete Book\n5. Back");
        System.out.print("Option: ");
        String choice = scanner.nextLine();
        try {
            switch (choice) {
                case "1":
                    List<Book> books = bookDAO.listBooks();
                    System.out.printf("%-5s | %-25s | %-20s | %-10s%n", "ID", "Title", "Author", "Status");
                    books.forEach(b -> System.out.printf("%-5d | %-25s | %-20s | %-10s%n", 
                        b.getBookId(), b.getTitle(), b.getAuthor(), b.getAvailabilityStatus()));
                    break;
                case "2":
                    System.out.print("Enter ID: "); int id = Integer.parseInt(scanner.nextLine());
                    System.out.print("Title: "); String t = scanner.nextLine();
                    System.out.print("Author: "); String a = scanner.nextLine();
                    System.out.print("Category: "); String c = scanner.nextLine();
                    bookDAO.addBook(id, t, a, c, "Available");
                    System.out.println("System Message: Book added successfully.");
                    break;
                case "3":
                    System.out.print("Enter Book ID: "); int uid = Integer.parseInt(scanner.nextLine());
                    System.out.print("New Status (Available/Borrowed): "); String s = scanner.nextLine();
                    bookDAO.updateBookStatus(uid, s);
                    System.out.println("System Message: Status updated.");
                    break;
                case "4":
                    System.out.print("Enter ID to Delete: "); int did = Integer.parseInt(scanner.nextLine());
                    System.out.print("Confirm Deletion? Y/N: ");
                    if(scanner.nextLine().equalsIgnoreCase("Y")) {
                        bookDAO.deleteBook(did);
                        System.out.println("System Message: Book deleted.");
                    }
                    break;
            }
        } catch (Exception e) { System.out.println("Error: Check your input format."); }
    }

    private void manageMembers() {
        System.out.println("\nMEMBER MANAGEMENT");
        System.out.println("1. View All Members\n2. Add Member\n3. Delete Member\n4. Back");
        System.out.print("Option: ");
        String choice = scanner.nextLine();
        try {
            if (choice.equals("1")) {
                List<Member> members = memberDAO.listMembers();
                System.out.printf("%-5s | %-20s | %-25s | %-10s%n", "ID", "Name", "Email", "Status");
                members.forEach(m -> System.out.printf("%-5d | %-20s | %-25s | %-10s%n", 
                    m.getMemberId(), m.getName(), m.getEmail(), m.getMembershipStatus()));
            } else if (choice.equals("2")) {
                System.out.print("ID: "); int id = Integer.parseInt(scanner.nextLine());
                System.out.print("Name: "); String n = scanner.nextLine();
                System.out.print("Email: "); String e = scanner.nextLine();
                memberDAO.addMember(id, n, e, "Active");
                System.out.println("System Message: Member registered.");
            } else if (choice.equals("3")) {
                System.out.print("Enter Member ID to delete: "); int did = Integer.parseInt(scanner.nextLine());
                memberDAO.deleteMember(did);
                System.out.println("System Message: Member removed.");
            }
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }

    private void manageBorrowing() {
        System.out.println("\nBORROWING RECORDS");
        System.out.println("1. List Borrowed Books\n2. New Borrowing\n3. Return Book\n4. Back");
        System.out.print("Option: ");
        String choice = scanner.nextLine();
        try {
            if (choice.equals("1")) {
                List<Borrowing> list = borrowDAO.listCurrentBorrowings();
                System.out.printf("%-5s | %-20s | %-20s | %-12s%n", "BID", "Book", "Member", "Due Date");
                list.forEach(b -> System.out.printf("%-5d | %-20s | %-20s | %-12s%n", 
                    b.getBorrowId(), b.getBookTitle(), b.getMemberName(), b.getDueDate()));
            } else if (choice.equals("2")) {
                System.out.print("Book ID: "); int bid = Integer.parseInt(scanner.nextLine());
                System.out.print("Member ID: "); int mid = Integer.parseInt(scanner.nextLine());
                System.out.print("Borrow Date (dd-MM-yyyy): "); String bDate = scanner.nextLine();
                System.out.print("Due Date (dd-MM-yyyy): "); String dDate = scanner.nextLine();
                borrowDAO.borrowBook(bid, mid, bDate, dDate);
                bookDAO.updateBookStatus(bid, "Borrowed");
                System.out.println("System Message: Borrowing record created.");
            } else if (choice.equals("3")) {
                System.out.print("Enter Borrow ID to return: "); int rid = Integer.parseInt(scanner.nextLine());
                borrowDAO.returnBook(rid);
                System.out.println("System Message: Return processed. Note: Manually update book status if needed.");
            }
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }

    private void searchRecords() {
        System.out.print("\nSearch across system by Title or Author or Name): ");
        String term = scanner.nextLine();
        try {
            System.out.println("\nSearch Results: Books");
            bookDAO.searchBooks(term).forEach(b -> System.out.println("ID: " + b.getBookId() + " | Title: " + b.getTitle() + " | Status: " + b.getAvailabilityStatus()));
            
            System.out.println("\nSearch Results: Members");
            memberDAO.searchMembers(term).forEach(m -> System.out.println("ID: " + m.getMemberId() + " | Name: " + m.getName()));
        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        }
    }
}