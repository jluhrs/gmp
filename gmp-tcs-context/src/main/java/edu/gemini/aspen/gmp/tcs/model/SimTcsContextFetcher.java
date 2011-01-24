package edu.gemini.aspen.gmp.tcs.model;

import java.io.*;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

/**
 * A TCS Context Fetcher that obtains its information from a file,
 * for simulation purposes.
 */
public class SimTcsContextFetcher implements TcsContextFetcher {

    private static final Logger LOG = Logger.getLogger(SimTcsContextFetcher.class.getName());

    public static final int TCS_CTX_LENGTH = 39;

    private List<double[]> _simData;

    int _currentPos;


    public SimTcsContextFetcher(InputStream stream) {

        if (stream == null) {
            throw new RuntimeException("Not a valid simulation data stream");
        }

        _simData = new ArrayList<double[]>();
        _currentPos = 0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader( new DataInputStream(stream)));
            String line;
            while ( (line = br.readLine()) != null) {

                if (line.matches("\\s*$")) { //an empty line, discard it
                    continue;
                }

                if (line.matches("\\s*#.*")) { //a comment, discard it
                    continue;
                }

                String[] values = line.split("\\s");
                if (values.length < TCS_CTX_LENGTH - 1) {
                    LOG.warning("Not enough elements for TCS Context on line :" + line);
                    continue;
                }

                double result[] = new double[TCS_CTX_LENGTH];
                //We store the elements starting in position 1. The
                //element in position 0 is reserved for time
                for (int i = 1; i < TCS_CTX_LENGTH; i++) {
                    result[i]  = Double.parseDouble(values[i - 1]);
                }
                _simData.add(result);
            }

            br.close();
            stream.close();


        }catch (IOException e) {
            throw new RuntimeException("Problems reading data stream", e);
        }
    }

    public SimTcsContextFetcher(String dataFile) throws FileNotFoundException {
        this(new FileInputStream(dataFile));
    }


    public double[] getTcsContext() throws TcsContextException {

        if (_simData.size() <= 0) return null;

        double[] result = _simData.get(_currentPos++);

        //store the timestamp in position 0, as seconds since 1970
        result[0] = System.currentTimeMillis() / 1000.0;
        //If the next position is beyond the available data, restart
        if (_currentPos >= _simData.size()) {
            _currentPos = 0;
        }
        return result;

    }
}
