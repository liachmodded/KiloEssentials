package org.kilocraft.essentials.util.messages.nodes;

public enum CommandMessageNode {
    EXECUTION_EXCEPTION_HELP("execution_exception_help");

    private String key;

    CommandMessageNode(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
