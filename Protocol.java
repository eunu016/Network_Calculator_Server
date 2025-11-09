public class Protocol {

    public static final String CMD_ADD = "ADD";
    public static final String CMD_SUB = "SUB";
    public static final String CMD_MUL = "MUL";
    public static final String CMD_DIV = "DIV";

    public static final String R_CODE_OK = "200";
    public static final String R_TYPE_ANSWER = "ANSWER";

    public static final String R_CODE_CLIENT_ERROR = "400";
    public static final String R_TYPE_DIV_BY_ZERO = "DIVIDE_BY_ZERO";
    public static final String R_TYPE_INVALID_ARGS = "INVALID_ARGS";
    public static final String R_TYPE_UNKNOWN_CMD = "UNKNOWN_COMMAND";

    public static final String DELIMITER = " ";

    public static String createResponse(String code, String type, String value) {
        return code + DELIMITER + type + DELIMITER + value;
    }
}