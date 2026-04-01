package com.stmarys.library;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {

    private static final String DB_URL = "jdbc:sqlite:library.db";

    public MemberDAO() {
        // This ensures the 'members' table exists.
        String createTableSQL = "CREATE TABLE IF NOT EXISTS members (" +
                "member_id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "email TEXT, " +
                "membership_status TEXT" +
                ")";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("FATAL ERROR: Could not create members table.");
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public void addMember(int memberId, String name, String email, String status) throws SQLException {
        String sql = "INSERT INTO members (member_id, name, email, membership_status) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            pstmt.setString(4, status);
            pstmt.executeUpdate();
        }
    }

    public void updateMember(Member member) throws SQLException {
        String sql = "UPDATE members SET name = ?, email = ?, membership_status = ? WHERE member_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getEmail());
            pstmt.setString(3, member.getMembershipStatus());
            pstmt.setInt(4, member.getMemberId());
            pstmt.executeUpdate();
        }
    }

    public void deleteMember(int memberId) throws SQLException {
        String sql = "DELETE FROM members WHERE member_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.executeUpdate();
        }
    }

    public List<Member> listMembers() throws SQLException {
        List<Member> memberList = new ArrayList<>();
        String sql = "SELECT * FROM members";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                memberList.add(new Member(
                        rs.getInt("member_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("membership_status")
                ));
            }
        }
        return memberList;
    }

    public List<Member> searchMembers(String searchTerm) throws SQLException {
        List<Member> memberList = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE name LIKE ? OR email LIKE ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchTerm + "%");
            pstmt.setString(2, "%" + searchTerm + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    memberList.add(new Member(
                            rs.getInt("member_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("membership_status")
                    ));
                }
            }
        }
        return memberList;
    }
}