package nl.huvema.huvsmaerp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class McpTool {
    
    private String name;
    private String description;
    @JsonProperty("inputSchema")
    private Map<String, Object> inputSchema;

    // Constructors
    public McpTool() {}

    public McpTool(String name, String description, Map<String, Object> inputSchema) {
        this.name = name;
        this.description = description;
        this.inputSchema = inputSchema;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Map<String, Object> getInputSchema() { return inputSchema; }
    public void setInputSchema(Map<String, Object> inputSchema) { this.inputSchema = inputSchema; }
}
