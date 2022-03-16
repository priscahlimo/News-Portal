public class ApiException extends RuntimeException {
    private final int StatusCode;

    public int getStatusCode() {
        return StatusCode;
    }

    public ApiException(String message, int statusCode) {
        super(message);
        StatusCode = statusCode;
    }
}
