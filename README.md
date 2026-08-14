# OpsPilot-AI-Powered-Incident-Intelligence-Resolution-Platform
OpsPilot is an AI-powered platform that detects, analyzes, and helps resolve software incidents by combining observability data, historical incidents, technical runbooks, machine learning, and intelligent agents.


The architecture


                         ┌───────────────────────────┐
                         │          USERS            │
                         │ DevOps • SRE • Engineers  │
                         └─────────────┬─────────────┘
                                       │
                                       ▼
                         ┌───────────────────────────┐
                         │       WEB DASHBOARD       │
                         │   React + TypeScript      │
                         └─────────────┬─────────────┘
                                       │
                                       ▼
                         ┌───────────────────────────┐
                         │       API GATEWAY         │
                         │    Spring Cloud Gateway   │
                         └─────────────┬─────────────┘
                                       │
             ┌─────────────────────────┼─────────────────────────┐
             ▼                         ▼                         ▼
   ┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
   │ Incident Service│       │ Service/Monitor │       │ Runbook Service │
   │   Spring Boot   │       │     Service     │       │   Spring Boot   │
   └────────┬────────┘       └────────┬────────┘       └────────┬────────┘
            │                         │                         │
            └─────────────────────────┼─────────────────────────┘
                                      ▼
                         ┌───────────────────────────┐
                         │     ORACLE DATABASE       │
                         │ Relational + Vector Data │
                         └─────────────┬─────────────┘
                                       │
                         ┌─────────────┴─────────────┐
                         ▼                           ▼
               ┌───────────────────┐       ┌────────────────────┐
               │   RAG / KNOWLEDGE │       │   AI / ML SERVICE  │
               │      LAYER        │       │      Python        │
               └─────────┬─────────┘       └──────────┬─────────┘
                         │                            │
                         └──────────────┬─────────────┘
                                        ▼
                            ┌────────────────────────┐
                            │    INCIDENT AGENT      │
                            │ Search • Logs • Metrics│
                            │ Runbooks • Diagnosis   │
                            └────────────────────────┘
                            

              
