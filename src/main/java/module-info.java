module my.app {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires java.compiler;
    requires java.datatransfer;
    requires java.desktop;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.web;
    requires javafx.fxml;

    requires org.kordamp.ikonli.antdesignicons;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.entypo;
    requires org.kordamp.ikonli.feather;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.ionicons4;

    // ⚠️ opens ≠ exports
    // opens é para reflection, exports é para uso público.
    // se usa reflection (Spring, @Component, etc.)
    // opens my_app.components;
    // opens my_app.data.contracts;

    // abrindo my_app inteiro por enquanto pra usos como reflection e etc
    opens my_app;
    opens my_app.contexts;
    opens my_app.hotreload;
}