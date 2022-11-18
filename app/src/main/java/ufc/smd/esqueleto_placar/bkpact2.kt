package ufc.smd.esqueleto_placar

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import data.Placar
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.charset.StandardCharsets
import java.util.ArrayDeque

/*
A ideia inicial que quero fazer pra interface do placar é a seguinte:
- O placar vai de 0 a 99 (implementado)
- O timer vai de 45:00 a 00:00
- Existe a possibilidade do acrescimo. Se timer <= 05:00, é possível incrementar o tempo em até 10 minutos de acrescimo, de 1 em 1 minuto com um botão tipo um mais (+)
- Se percorrimento do tempo atingir o limite (00:00), não é possível adicionar mais placar ao jogo.
- Deve-se apertar o botão de segundo tempo para recolocar nos 45:00.
 */

open class PlacarActivity2 : AppCompatActivity() {
    lateinit var placar:Placar
    lateinit var tvResultadoRed: TextView
    lateinit var tvResultadoBlue : TextView
    lateinit var salvarResultado : Button
    var gameRed = 0
    var gameBlue = 0
    var ctrlZ = ArrayDeque<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placar)

        placar= getIntent().getExtras()?.getSerializable("placar") as Placar
        tvResultadoRed = findViewById(R.id.tvPlacarRed)
        tvResultadoBlue = findViewById(R.id.tvPlacarBlue)
        salvarResultado = findViewById(R.id.btSalvarPlacar)

        //Mudar o nome da partida
        val tvNomePartida=findViewById(R.id.tvNomePartida2) as TextView
        tvNomePartida.text=placar.nome_partida
        ultimoJogos()
    }

    fun alteraPlacarRed (v:View){
        ctrlZ.push(gameRed+100)
        gameRed++

        if (gameRed > 99){
            gameRed = 0
        }
        placar.resultado = gameBlue.toString() + " vs " + gameRed.toString()
        tvResultadoRed.text=gameRed.toString()
    }

    fun alteraPlacarBlue (v:View){
        ctrlZ.push(gameBlue)
        gameBlue++

        if (gameBlue > 99){
            gameBlue = 0
        }

        placar.resultado = gameBlue.toString() + " vs " + gameRed.toString()
        tvResultadoBlue.text=gameBlue.toString()
    }

    fun stackCtrlZ(v: View){
        if (ctrlZ.isEmpty()) {
            return
        } else if (ctrlZ.first() > 99){
            gameRed = ctrlZ.first()-100
            placar.resultado = gameBlue.toString() + " vs " + gameRed.toString()
            tvResultadoRed.text=gameRed.toString()
        } else if (ctrlZ.first() <= 99) {
            gameBlue = ctrlZ.first()
            placar.resultado = gameBlue.toString() + " vs " + gameRed.toString()
            tvResultadoBlue.text=gameBlue.toString()
        }
        ctrlZ.removeFirst()
    }

/*
    fun vibrar (v:View){
        val buzzer = this.getSystemService<Vibrator>()
         val pattern = longArrayOf(0, 200, 100, 300)
         buzzer?.let {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                 buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
             } else {
                 //deprecated in API 26
                 buzzer.vibrate(pattern, -1)
             }
         }

    }
*/

    fun saveGame(v: View) {

        val finalRed = tvResultadoRed.toString()
        val finalBlue = tvResultadoBlue.toString()

        val sharedFilename = "PreviousGames"
        val sp: SharedPreferences = getSharedPreferences(sharedFilename, Context.MODE_PRIVATE)
        var edShared = sp.edit()

        //Salvar o número de jogos já armazenados
        var numMatches= sp.getInt("numberMatch",0) + 1
        edShared.putInt("numberMatch", numMatches)

        //Escrita em Bytes de Um objeto Serializável
        var dt= ByteArrayOutputStream()
        var oos = ObjectOutputStream(dt);
        oos.writeObject(placar);

        //Salvar como "match"
        edShared.putString("match"+numMatches, dt.toString(StandardCharsets.ISO_8859_1.name()))
        edShared.commit() //Não esqueçam de comitar!!!

    }

    fun lerUltimosJogos(v: View){
        val sharedFilename = "PreviousGames"
        val sp: SharedPreferences = getSharedPreferences(sharedFilename, Context.MODE_PRIVATE)

        var meuObjString:String= sp.getString("match1","").toString()
        if (meuObjString.length >=1) {
            var dis = ByteArrayInputStream(meuObjString.toByteArray(Charsets.ISO_8859_1))
            var oos = ObjectInputStream(dis)
            var placarAntigo:Placar=oos.readObject() as Placar
            Log.v("SMD26",placar.resultado)
        }
    }




    fun ultimoJogos () {
        val sharedFilename = "PreviousGames"
        val sp:SharedPreferences = getSharedPreferences(sharedFilename,Context.MODE_PRIVATE)
        var matchStr:String=sp.getString("match1","").toString()
        // Log.v("PDM22", matchStr)
        if (matchStr.length >=1){
            var dis = ByteArrayInputStream(matchStr.toByteArray(Charsets.ISO_8859_1))
            var oos = ObjectInputStream(dis)
            var prevPlacar:Placar = oos.readObject() as Placar
            Log.v("PDM22", "Jogo Salvo:"+ prevPlacar.resultado)
        }

    }
}