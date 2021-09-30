# mtxml
**Work-in-progress**

mtxml is a parser for converting SWIFT MT messages following the [SWIFT MT Standard](https://www2.swift.com/knowledgecentre/products/Standards%20MT) into XML. 

Alongside with the Java code, *template.yml* specifies an AWS SAM template. This template can be used to deploy a REST-api using the AWS Lambda and APIGateway service. This way MTMessages can be parsed via GET-requests. This can be done via the SAM CLI. Some API-key verification is also added.

Usage for deployment to AWS:
```console
sam build
sam deploy
```

Currently only supports the 2020 MT Standard.