# Coupon Exercise Challenge 

Given a coupon of a certain amount for free will allow you to buy the largest amount of items
that do not exceed the total amount.

## Getting Started

These instructions will allow you to execute the challenge carried out for the interview.

### Executing

To apply a coupon using Swagger-ui, enter *http://couponapi-env.eba-cxdmpmmc.us-east-2.elasticbeanstalk.com/swagger-ui.html* and try out '/coupon' endpoint.

```
{
    "item_ids": ["MLA1", "MLA2", "MLA3", "MLA4", "MLA5"],
    "amount": 500
}
```

or calling

```
curl -X POST "accept: */*" -H "Content-Type: application/json"'
-d '{
    "item_ids": ["MLA1", "MLA2", "MLA3", "MLA4", "MLA5"],
    "amount": 500
}'
'http://couponapi-env.eba-cxdmpmmc.us-east-2.elasticbeanstalk.com/coupon'
```

Response:
```
{
    "item_ids": ["MLA1", "MLA2", "MLA4", "MLA5"],
    "total": 480
}
```
*Must be tested with valid item_ids*


## Built With

* [Java](https://www.java.com/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [Spring Boot](https://spring.io/projects/spring-boot) - The web framework used
* [Swagger](https://swagger.io/) - API documentation


## Authors

* **Santiago Ambrosetti**
