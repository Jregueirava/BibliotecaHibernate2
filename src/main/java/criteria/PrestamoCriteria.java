package criteria;

import modelo.Prestamo;

import java.time.LocalDate;

public class PrestamoCriteria {
    private Prestamo.EstadoPrestamo estadoPrestamo;
    private LocalDate iniFechaInicio;
    private LocalDate finFechaInicio;

    public Prestamo.EstadoPrestamo getEstadoPrestamo() {
        return estadoPrestamo;
    }

    public void setEstadoPrestamo(Prestamo.EstadoPrestamo estadoPrestamo) {
        this.estadoPrestamo = estadoPrestamo;
    }

    public LocalDate getFinFechaInicio() {
        return finFechaInicio;
    }

    public void setFinFechaInicio(LocalDate finFechaInicio) {
        this.finFechaInicio = finFechaInicio;
    }

    public LocalDate getIniFechaInicio() {
        return iniFechaInicio;
    }

    public void setIniFechaInicio(LocalDate iniFechaInicio) {
        this.iniFechaInicio = iniFechaInicio;
    }

    public boolean isPresentEstadoPrestamo(){
        return this.estadoPrestamo!=null;
    }

    public boolean isPresentFechaInicio(){
        return this.iniFechaInicio!=null && this.finFechaInicio!=null;
    }
}
