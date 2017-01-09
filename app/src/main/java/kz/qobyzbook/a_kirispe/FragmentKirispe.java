package kz.qobyzbook.a_kirispe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kz.qobyzbook.R;

/**
 * Created by zhan on 11/28/16.
 */

public class FragmentKirispe extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_kirispe, container, false);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String lang = preferences.getString("lang", "kk");
        TextView intro = (TextView)v.findViewById(R.id.textView12);
        if (lang.equals("kk")) {
            intro.setText("Қобыз туралы. Аспаптың шығу тарихы, жасалу жолдары,  құрылысы, атауының мән-мағынасы және оны тарту жолдары берілген.  \n" +
                    "\t•\tКүйлердің аңыздары және әдістемелік нұсқаулар. Орындалатын әр күйге орындаушылық талдау жасалып, әдістемелік нұсқаулар көрсетіледі және күйдің шығу тарихы мен атауының мән-мағынасы жайында ақпарат беріледі. \n" +
                    "\t•\tТарихи тұлғалар. Қорқыт, Ықылас және т.б. тарихи тұлғалардан бастап, осы күнге дейін қобыз аспабын насихатап, тартып, еңбек етіп жүрген ұстаздер мен күйші-орындаушылардың өмірі мен шығармашылығы жайында мағлұмат беріледі. \n" +
                    "\t•\tБейне сабақтар. Қобыз күйлерінің бейне жазбалары берілген. Оқушы өзіне керекті күйлерді бейне жазбалар арқылы үйрене алады. Орындаушының қолдары қосымша үлкейтіліп көрсетіледі.\n" +
                    "\t•\tКүйлердің аудио нұсқалары. Бейне және жазба материалдармен қатар аудио жабалар да беріледі. Қобыз классиктерінің орындауындағы күйлер мен қазіргі заманға қобызшылардың орындауындағы күйлер берілген. \n" +
                    "\t•\tКүйлердің ноталық үлгілері. Орындаушы бейне жазбаны қарап отырып, орындалып жатқан күйдің нотасына көз жүгірте алады. \n" +
                    "\t•\tКүйпарақ (плейлист). Орындаушы өзіне ұнайтын, қажетті күйлер мен әндерді өзінің күйпарағына көшіріп ала алады. \n" +
                    "\t•\tЖаңалықтар. Қобыз әлемінде болып жатқан жаңалықтар, конкурс талаптары, нәтижелері және т.б. дүниелер осы бөлімде орын алған.");
        } else {
            intro.setText("About kobyz. The history of origin of the instrument, the handcrafting technique,  kobyz’s structure, the meaning of its name and the methods of playing kobyz are described.  \n" +
                    "\t•\tThe kui legends and methodological instructions. Every kui that is performed is analysed from executional view and given methodological instructions, an information about the history of origin of the kui and the meaning of its name is provided. \n" +
                    "\t•\tHistorical figures. An information about the life and art of Korkut, Ykylas and other historical figures of our times, who played the kobyz instrument and have advocated it, and taught it to younger generation, is provided. \n" +
                    "\t•\tMovie tutorials. The video recordings of kobyz kuis are provided. The student can learn the necessary kuis with the help of movie tutorials. The performer’s hands are zoomed in.\n" +
                    "\t•\tAudio versions of the kuis. Along with movie tutorials and recording materials audio recordings are provided. The kuis performed by kobyz classics and kuis performed by modern kobyz performers are provided. \n" +
                    "\t•\tMusical notes of the kuis. Watching the movie tutorial, the performer can look through the musical notes of the kui. \n" +
                    "\t•\tKui-page (playlist). The performer can copy favorite kuis and songs to his kui-page. \n" +
                    "\t•\tNews. The news taking place in kobyz world, contest requirements, results and other information are provided in this section.");
        }
        return v;
    }
}
