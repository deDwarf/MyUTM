package core.exceptions;

public class InvalidDateFormatException extends BadRequestException {

    public InvalidDateFormatException(String fieldName, String value) {
        super(String.format(
                "Failed to parse '%s' field for value <%s>. Check if date follows 'yyyy-MM-dd' format",
                fieldName, value));
    }

}
