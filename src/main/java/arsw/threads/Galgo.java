package arsw.threads;

/**
 * Un galgo que puede correr en un carril
 *
 * @author rlopez
 *
 */
public class Galgo extends Thread {

    private int paso;
    private Carril carril;
    RegistroLlegada regl;
    private boolean pausado = false;
    private Object lock = new Object();

    public Galgo(Carril carril, String name, RegistroLlegada reg) {
        super(name);
        this.carril = carril;
        paso = 0;
        this.regl = reg;
    }

    public void pausar() {
        pausado = true;
    }

    public void continuar() {
        synchronized (lock) {
            pausado = false;
            lock.notifyAll();
        }
    }

    public void corra() throws InterruptedException {
        while (paso < carril.size()) {
            synchronized (lock) {
                while (pausado) {
                    lock.wait();
                }
            }

            Thread.sleep(100);
            carril.setPasoOn(paso++);
            carril.displayPasos(paso);

            if (paso == carril.size()) {
                carril.finish();
                synchronized (regl) {
                    int ubicacion = regl.getUltimaPosicionAlcanzada();
                    regl.setUltimaPosicionAlcanzada(ubicacion + 1);
                    System.out.println("El galgo " + this.getName() + " llego en la posicion " + ubicacion);
                    if (ubicacion == 1) {
                        regl.setGanador(this.getName());
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            corra();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
