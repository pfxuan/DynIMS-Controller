package edu.clemson.bigdata.tls.pbs.parser;

/**
 * A parser used by PBS Java API.
 *
 */
public interface Parser<X, T> {

    T parse(X x) throws Exception;

}
