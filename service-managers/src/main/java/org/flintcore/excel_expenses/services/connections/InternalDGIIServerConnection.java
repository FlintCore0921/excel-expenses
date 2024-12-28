package org.flintcore.excel_expenses.services.connections;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class InternalDGIIServerConnection extends ExeFileConnection {
    public static InternalDGIIServerConnection initOn(String fileName) {
        InternalDGIIServerConnection internalExeConnection = new InternalDGIIServerConnection(fileName);
        internalExeConnection.init();
        return internalExeConnection;
    }

    private InternalDGIIServerConnection(String file_path) {
        super(file_path);
    }
}
