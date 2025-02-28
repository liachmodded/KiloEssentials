package org.kilocraft.essentials.util.messages.nodes;

public enum ArgExceptionMessageNode {
    TIME_ARGUMENT_INVALID("time.invalid"),
    TIME_ARGUMENT_ERROR("time.unexpected")
    ;

    private String key;

    ArgExceptionMessageNode(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

}
