module ru.yandex.practicum.ewmstatsservice {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.yandex.practicum.ewmstatsservice to javafx.fxml;
    exports ru.yandex.practicum.ewmstatsservice;
}