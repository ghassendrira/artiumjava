package services;

import java.sql.SQLException;
import java.util.List;
public interface Service<C> {

    void ajouter(C c) throws SQLException;

    void modifier(C c) throws SQLException;

    void supprimer(C c) throws SQLException;

    List<C> recuperer() throws SQLException;
}