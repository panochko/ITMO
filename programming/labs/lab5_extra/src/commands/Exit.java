package commands;

public final class Exit extends AbstractCommand{
    @Override
    public void execute(String arguments){System.exit(0);}
}
