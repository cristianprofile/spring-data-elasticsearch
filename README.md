# spring-data-elasticsearch
Testing Spring data elasticsearch 



Testing Spring Data Elasticsearch  using TransportClient. 


-  ElasticsearchTemplate.save
-  ElasticsearchTemplate.delete
-  ElasticsearchTemplate.findAll

ElasticsearchTemplate.queryForPage using 

-  Bool must  (AND)

-  Bool must + Bool shoud Bool must (OR)


Testing create sample assert for your logtrace when a test it run

    @Rule
    public OutputCapture capture = new OutputCapture();
    
    
      logger.info("Test begin process");
      logger.info("Test end process");
      assertThat(capture.toString()).contains("Test begin process");
      assertThat(capture.toString()).contains("Test end process");




