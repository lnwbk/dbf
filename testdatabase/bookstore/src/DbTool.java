import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbTool {
    private Connection conn;

    // ปรับ user / password ถ้าจำเป็น
    public DbTool() throws ClassNotFoundException, SQLException {
        String url = "jdbc:mysql://localhost:3306/bookstore_db?serverTimezone=UTC&useSSL=false";
        String user = "root";
        String pass = ""; // ใส่รหัสผ่านถ้ามี
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(url, user, pass);
    }

    public List<App> getAllBooks() throws SQLException {
        String sql = "SELECT * FROM bookstore ORDER BY name";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<App> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new App(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getInt("amount"),
                    rs.getInt("price")
                ));
            }
            return list;
        }
    }

    public App getBookById(String id) throws SQLException {
        String sql = "SELECT * FROM bookstore WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new App(rs.getString("id"), rs.getString("name"),
                                    rs.getInt("amount"), rs.getInt("price"));
                }
            }
        }
        return null;
    }

    public boolean insertBook(App b) throws SQLException {
        String sql = "INSERT INTO bookstore (id,name,amount,price) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getId());
            ps.setString(2, b.getName());
            ps.setInt(3, b.getAmount());
            ps.setInt(4, b.getPrice());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateBook(App b) throws SQLException {
        String sql = "UPDATE bookstore SET name=?, amount=?, price=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getName());
            ps.setInt(2, b.getAmount());
            ps.setInt(3, b.getPrice());
            ps.setString(4, b.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteBook(String id) throws SQLException {
        String sql = "DELETE FROM bookstore WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<App> searchBooks(String keyword) throws SQLException {
        String sql = "SELECT * FROM bookstore WHERE id LIKE ? OR name LIKE ? ORDER BY name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k);
            try (ResultSet rs = ps.executeQuery()) {
                List<App> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new App(rs.getString("id"), rs.getString("name"),
                                      rs.getInt("amount"), rs.getInt("price")));
                }
                return list;
            }
        }
    }

    public void close() {
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
