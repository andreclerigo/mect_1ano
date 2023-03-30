import java.io.Serializable;

/**
 *  Este tipo de dados define um exemplo de um <em>record</em> para armazenamento de informação.
 *
 *    @author António Rui Borges
 *    @version 1.1
 */

public class Record implements Serializable
{
   /**
    *  Identificador interno para serialização
    */

    private static final long serialVersionUID = 20140404L;

   /**
    *   Número de identidicação interna
    */

    public int nEmp;

   /**
    *   Nome da entidade
    */

    public String nome;

   /**
    *   Inicialização de um objecto.
    */

    public Record (int nEmp, String nome)
    {
       this.nEmp = nEmp;
       this.nome = nome;
    }
}
