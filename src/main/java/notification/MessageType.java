package notification;

import lombok.Getter;

enum MessageType {
    ERROR("Error"),
    INFO("Info");

    @Getter
    private String typeName;

    MessageType(String type) {
        typeName = type;
    }
}
