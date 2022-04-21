(ns alto-logica.util.minio-client
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.java.io :as io])
  (:import [java.io File]
           [io.minio BucketExistsArgs]
           [io.minio MakeBucketArgs]
           [io.minio MinioClient]
           [io.minio PutObjectArgs]
           [io.minio GetObjectArgs]
           [io.minio RemoveObjectArgs]
           [io.minio.errors MinioException]
           [java.io IOException]
           [java.security InvalidKeyException]
           [java.security NoSuchAlgorithmException]
           ))

;; (defonce end-point "https://play.min.io")
;; (defonce bucket-name "alto-logica")
;; (defonce access-key-id "Q3AM3UQ867SPQQA43P2F")
;; (defonce access-key "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG")

(defonce end-point "http://localhost:9000")
(defonce bucket-name "alto-logica")
(defonce access-key-id "minio")
(defonce access-key "password")

(defn- get-mc []
  (let [mc (-> (MinioClient/builder)
               (.endpoint end-point)
               (.credentials access-key-id access-key)
               (.build))]
    (when-not (.bucketExists mc (-> (BucketExistsArgs/builder)
                                    (.bucket bucket-name)
                                    (.build)))
      (.makeBucket mc (-> (MakeBucketArgs/builder)
                          (.bucket bucket-name)
                          (.build))))
    mc))

(def mc (get-mc))

(defn put-doc [uri is size]
  (.putObject mc (-> (PutObjectArgs/builder)
                              (.bucket bucket-name)
                              (.object uri)
                              (.stream is size -1)
                              (.build))))

(defn get-doc-is [uri]
  (.getObject mc (-> (GetObjectArgs/builder)
                     (.bucket bucket-name)
                     (.object uri)
                     (.build))))

(defn delete-doc [uri]
  (.removeObject mc (-> (RemoveObjectArgs/builder)
                     (.bucket bucket-name)
                     (.object uri)
                     (.build))))
