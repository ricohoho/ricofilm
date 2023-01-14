package ricohoho.ffmpeg;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.ffprobe.Chapter;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import com.github.kokorin.jaffree.ffprobe.data.FormatParser;
import com.github.kokorin.jaffree.ffprobe.data.JsonFormatParser;
import com.github.kokorin.jaffree.StreamType;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StreamFilm {
    String info;
    List<Document> InfoFilmSstreams = null;
    File fichierFilm;
    private static final Logger LOGGER = LoggerFactory.getLogger( "StreamFilm" );

    /**
     * Constructor
     * @param fichierFilm
     */
    public  StreamFilm(File fichierFilm) {
        this.fichierFilm=fichierFilm;
    }

    /**
     * kist bson info Fichier Film
     * @return
     */
    public List<Document> getInformationsFile() {
        LOGGER.info( "getInfo Début" );
        FormatParser formatParser=new JsonFormatParser() ;
        this.InfoFilmSstreams = new ArrayList<>();
        this.info="";
        LOGGER.info( "Fichier : " +fichierFilm.getAbsolutePath());
        FFprobeResult result = null;
        try {
            result = FFprobe.atPath()
                    .setShowStreams(true)
                    .setInput(fichierFilm.getAbsolutePath())
                    .setLogLevel(LogLevel.DEBUG)
                    .setFormatParser(formatParser)
                    .execute();
        } catch (Exception e) {
            LOGGER.info( "Exception getInformationsFile"+e.toString() );
        }
        if (result !=null) {
            LOGGER.info( "result ! nulll");
            for (Stream stream : result.getStreams()) {

                Document info1Film = new Document();


                String sInfo = "";
                String codecType = stream.getCodecType().toString();
                LOGGER.info( "---------------------------->"+codecType +"["+stream.getIndex() +"]");
                sInfo = "Stream #" + stream.getIndex() + "]"
                        + " Codec type: [" + stream.getCodecType().code() + "]"
                        + " Codec Name : [" + stream.getCodecName() + "]"
                        + " duration: " + stream.getDuration() + " seconds"
                        + " Codec Long Name : [" + stream.getCodecLongName() + "]"
                        + " Profil : [" + stream.getProfile() + "]" //Main 10
                        + " TimeBase : [" + stream.getTimeBase() + "]" //1/1000
                        + " Tag BPS : [" +stream.getTag("BPS") + "]" //
                        + " Tag DURATION : [" +stream.getTag("DURATION") + "]" //
                        + " Tag NUMBER_OF_FRAMES : [" +stream.getTag("NUMBER_OF_FRAMES") + "]"
                        + " Tag NUMBER_OF_BYTES : [" +stream.getTag("NUMBER_OF_BYTES") + "]"
                                ;

                info1Film.put("index",stream.getIndex());
                info1Film.put("CodecType",stream.getCodecType().toString());
                info1Film.put("CodecName",stream.getCodecName());
                info1Film.put("duration",stream.getDuration());
                info1Film.put("CodecLongName",stream.getCodecLongName());
                info1Film.put("TimeBase",stream.getTimeBase());
                info1Film.put("Profile",stream.getProfile());
                info1Film.put("tag-BPS",stream.getTag("BPS"));
                info1Film.put("tag-DURATION",stream.getTag("DURATION"));
                info1Film.put("tag-NUMBER_OF_FRAMES",stream.getTag("NUMBER_OF_FRAMES"));
                info1Film.put("tag-NUMBER_OF_BYTES",stream.getTag("NUMBER_OF_BYTES"));



                if (codecType.equals("VIDEO")) {
                    sInfo = sInfo + "\r\n *Résolution  : [" + stream.getCodedWidth() + "x" + stream.getCodedHeight() + "]"
                            + " *image par seconde : [" + stream.getAvgFrameRate() + "]"//24000/1001
                            //+ " Codec ChromaLocation  : [" + stream.getChromaLocation()+"]"
                            + " BitRate : [" + stream.getBitRate() + "]" //Null
                            + " Extradata : [" + stream.getExtradata() + "]" //Null
                            + " *Format des pixel : [" + stream.getPixFmt() + "]" //yuv420p10le
                            + " Frame : [" + stream.getNbFrames() + "]" //Null
                            + " *Image de ref : [" + stream.getRefs() + "]" //Null
                            + " *Ratio d’aspect : [" + stream.getDisplayAspectRatio() + "]" //Null
                            + " getRFrameRate : [" + stream.getRFrameRate() + "]" //Null
                            + " *Niveau : [" + stream.getLevel() + "]" //Null!
                            + " getIndex : [" + stream.getIndex() + "]" //Null!
                            + " Id : [" + stream.getId() + "]" //Null!
                            + " getMaxBitRate : [" + stream.getMaxBitRate() + "]" //Null!
                            + " getNbFrames : [" + stream.getNbFrames() + "]" //Null!


                    ;
                    String AvgFrameRate= "";
                    if (stream.getAvgFrameRate()!= null) AvgFrameRate = stream.getAvgFrameRate().toString();
                    info1Film.put("AvgFrameRate",AvgFrameRate);
                    info1Film.put("PixFmt",stream.getPixFmt());


                } else if (codecType.equals("AUDIO") || codecType.equals("SUBTITLE") ) {
                    sInfo = sInfo
                            + "\r\n "
                            + " Chaine : [" + stream.getChannels() + "]" //6 chaines
                            + "*Taux d'echantillonage : [" + stream.getSampleRate() + "]" //48 000 Hz
                            + " Channel : [" + stream.getChannelLayout() + "]" //5.1
                            + " Tag langauge : [" +stream.getTag("language") + "]" //
                            + " Tag title : [" +stream.getTag("title") + "]"; //

                    info1Film.put("Channels",stream.getChannels());
                    info1Film.put("SampleRate",stream.getSampleRate());
                    info1Film.put("ChannelLayout",stream.getChannelLayout());
                    info1Film.put("tag-language",stream.getTag("language"));
                    info1Film.put("tag-title",stream.getTag("title"));


                    //+ " getProbeData : [" + stream.getSideDataList().listIterator()+"]" //Null
                    ;
                } else if (codecType.equals("xxx")) {
                    sInfo = sInfo;
                    //+ " TimeBase : [" + stream.getTimeBase() + "]" //1/1000
                }


                System.out.println(sInfo);
                LOGGER.info( sInfo);
                this.info = this.info + sInfo;
                this.InfoFilmSstreams.add(info1Film);
            }
        }
        return this.InfoFilmSstreams;
    }
}
