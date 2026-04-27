package commands;

import java.io.Serial;

public final class Exit extends AbstractCommand{
    public Exit(String commandName) {
        super(commandName);
    }
    @Serial
    private static final long serialVersionUID = -8113014267628800468L;

    @Override
    public void exit(){System.exit(0);}
}
