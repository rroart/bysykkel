package roart.model;

public class BysykkelStativ {
    Integer id;
    
    String navn;
    
    Integer antallLaaser;
    
    Integer antallLedigeSykler;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public Integer getAntallLaaser() {
        return antallLaaser;
    }

    public void setAntallLaaser(Integer antallLaaser) {
        this.antallLaaser = antallLaaser;
    }

    public Integer getAntallLedigeSykler() {
        return antallLedigeSykler;
    }

    public void setAntallLedigeSykler(Integer antallLedigeSykler) {
        this.antallLedigeSykler = antallLedigeSykler;
    }

    @Override
    public String toString() {
        return navn + " har " + antallLedigeSykler + " ledige sykler av " + antallLaaser;
    }
}
