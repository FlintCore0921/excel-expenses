package org.flintcore.excel_expenses.services.exceptions;

public class ServerConnectionException extends RuntimeException {
    public ServerConnectionException() {
        super("Unable to communicate with server connection.");
    }
}
