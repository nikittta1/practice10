package org.example;

import java.sql.*;

public class Main {
    private static Connection connection;
    private static Statement statement;

    public static void main(String[] args) {
        createTable();
        fillingTable();
        task();
    }
    public static void createTable() {
        try {
            connection();
            statement.executeUpdate("drop table if exists progress, subjects, students;");
            statement.executeUpdate("create table students(" +
                    "id serial PRIMARY KEY, " +
                    "name varchar NOT NULL, " +
                    "passportSeries int NOT NULL, " +
                    "passportNumber int NOT NULL," +
                    "UNIQUE(passportSeries)," +
                    "UNIQUE(passportNumber)" +
                    ")");
            statement.executeUpdate("create table subjects(" +
                    "nameSubjects varchar PRIMARY KEY)");

            statement.executeUpdate("create table progress(" +
                    "id serial, " +
                    "idStudent integer," +
                    "foreign key (idStudent) references students(id) on delete cascade, " +
                    "nameSubjects varchar NOT NULL, " +
                    "FOREIGN KEY (nameSubjects) REFERENCES subjects(nameSubjects) ON DELETE CASCADE," +
                    "score smallint NOT NULL," +
                    "CHECK(score >= 2 AND score <= 5)" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnection();
        }
    }

    public static void fillingTable() {
        try {
            createTable();
            connection();
            statement.executeUpdate("insert into students(name, passportSeries, passportNumber) VALUES ('Женя', 3318, 123465)");
            statement.executeUpdate("insert into students(name, passportSeries, passportNumber) VALUES ('Катя', 3319, 123123)");
            statement.executeUpdate("insert into students(name, passportSeries, passportNumber) VALUES ('Юра', 3320, 214214)");
            statement.executeUpdate("insert into students(name, passportSeries, passportNumber) VALUES ('Витя', 3321, 124767)");
            statement.executeUpdate("insert into students(name, passportSeries, passportNumber) VALUES ('Аня', 3322, 322123)");

            statement.executeUpdate("insert into subjects(nameSubjects) VALUES ('Химия')");
            statement.executeUpdate("insert into subjects(nameSubjects) VALUES ('Физика')");
            statement.executeUpdate("insert into subjects(nameSubjects) VALUES ('Физ-ра')");
            statement.executeUpdate("insert into subjects(nameSubjects) VALUES ('Математика')");

            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (1, 'Физика', 5)");
            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (2, 'Физика', 4)");
            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (3, 'Физика', 2)");
            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (4, 'Физика', 5)");
            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (5, 'Физика', 5)");

            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (1, 'Математика', 4)");
            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (2, 'Математика', 5)");
            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (3, 'Математика', 3)");
            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (4, 'Математика', 5)");
            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (5, 'Математика', 4)");

            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (1, 'Физ-ра', 4)");
            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (2, 'Физ-ра', 3)");
            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (3, 'Физ-ра', 5)");
            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (4, 'Физ-ра', 3)");
            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (5, 'Физ-ра', 4)");

            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (1, 'Химия', 3)");
            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (2, 'Химия', 2)");
            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (3, 'Химия', 4)");
            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (4, 'Химия', 4)");
            statement.executeUpdate("insert into progress(idStudent, nameSubjects, score) VALUES (5, 'Химия', 5)");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnection();
        }
    }

    public static void task() {
        try {
            connection();

            System.out.println("- Студенты сдавшие физику больше чем на 3: ");
            ResultSet rs = statement.executeQuery("SELECT s.id, s.name, score FROM progress JOIN students s on progress.idStudent = s.id WHERE score > 3 and nameSubjects = 'Физика';");
            while (rs.next()) {
                String name = rs.getString("name");
                int idStudent = rs.getInt("id");
                int score = rs.getInt("score");
                System.out.println(name + " (id: " + idStudent + ") сдал на " + score);
            }

            rs = statement.executeQuery("SELECT avg(score), idstudent\n" +
                    "FROM progress\n" +
                    "WHERE idStudent = (SELECT id FROM students WHERE name = 'Юра')\n" +
                    "group by idstudent;");
            if(rs.next()){
                System.out.println("\n- Средний балл Юры (id: " + rs.getInt("idstudent") + ") по всем предметам:");
                System.out.println(String.format("%.2f", rs.getFloat("avg")).replace(",", "."));
            }

            System.out.println("\n- Средний балл всех студентов по математике:");
            rs = statement.executeQuery("select avg(score) from progress where namesubjects = 'Математика'");
            if (rs.next()){
                System.out.println(rs.getFloat("avg"));
            }

            System.out.println("\n- Три предмета, которые сдали наибольшее кол-во студентов:");
            rs = statement.executeQuery("SELECT nameSubjects, COUNT(*) AS succes\n" +
                    "FROM progress\n" +
                    "WHERE score > 2\n" +
                    "GROUP BY nameSubjects ORDER BY succes DESC\n" +
                    "limit 3;");
            while (rs.next()) {
                String nameSub = rs.getString("nameSubjects");
                int count = rs.getInt("succes");
                String chel = "человек";
                if (2 <= count && count <= 4){
                    chel = "человека";
                }
                System.out.println(nameSub + " (сдало " + count + " " + chel + ")");
            }

            } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            disconnection();
        }
    }

    public static void connection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres1");
            statement = connection.createStatement();

        } catch (ClassNotFoundException | SQLException e) {
            throw new SQLException("Не удалось подключиться");
        }
    }

    public static void disconnection() {
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
