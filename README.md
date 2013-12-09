### couch_replication

Cloudant - CouchDB based replication framework between mobile phone and Cloudant/CouchDB server.

### Basic Test

The following command retrieves changed data on a_user1 database since revision number 0(from the beginning).

<pre>
shell> curl -s 'http://ighlyesesedisentstoldneg:cOlugjOsPXtkHyR1SPvNDYME@jerryj3.cloudant.com/a_user1/_changes?include_docs=true&descending=true&since=0' | jq .<br>
{"seq":"28-g1AAAAETeJzLYWBgYMlgTmGQTUlKzi9KdUhJMtUrzsnMS9dLzskvTUnMK9HLSy3JASpjSmRIsv___39WBlMiay5QgD3F1NgszcIEVbsZLu1JDkAyqR5qAifYBDPT5FRjC1MiTchjAZIMDUAKaMh-kCkMYFNMDcxTLFNSSDLlAMQUsFv4wKaYJBkmJyYmZQEAGMtUAw","id":"22c4facabc3609a225c0bedb71028258","changes":[{"rev":"5-e61a5f268d6d4ac24b2bfb72c50b4cfc"}]}
</pre>


jq, a command line JSON processor, can be downloaded from https://github.com/stedolan/jq.

If there is no new modifiction but you want to wait for new one, then use longpoll=true.

<pre>
curl -s 'http://ighlyesesedisentstoldneg:cOlugjOsPXtkHyR1SPvNDYME@jerryj3.cloudant.com/a_user1/_changes?include_docs=true&descending=true&since=<lastseq>&feed=longpoll'
</pre>

or use continuous for processing the feed forever. 

<pre>
curl -s 'http://ighlyesesedisentstoldneg:cOlugjOsPXtkHyR1SPvNDYME@jerryj3.cloudant.com/a_user1/_changes?include_docs=true&descending=true&since=<lastseq>&feed=continuous'
</pre>


