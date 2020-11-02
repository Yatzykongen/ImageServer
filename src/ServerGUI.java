public class ServerGUI
{
    public void ServerGUI()
    {
        try {
            int state = 0;
            int level = 0;
            boolean direction = true;
            while(true) {
                switch (state) {
                    case 0:
                        System.out.println("Lining up");
                        Thread.sleep(5000);
                        state = 1;
                        break;
                    case 1:
                        System.out.println("Move forward");
                        Thread.sleep(1000);
                        state = 2;
                        break;
                    case 2:
                        if (direction && level < 3) {
                            System.out.println("Move elivator up");
                            Thread.sleep(1000);
                            level++;
                        }
                        if (!direction && level > 0) {
                            System.out.println("Move elivator down");
                            Thread.sleep(1000);
                            level--;
                        }
                        if (level < 0 || level > 3) {
                            direction = !direction;
                            state = 1;
                        }
                        break;
                }
            }
        }catch (InterruptedException e)
        {
            System.err.println(e.getMessage());
        }
    }
}
