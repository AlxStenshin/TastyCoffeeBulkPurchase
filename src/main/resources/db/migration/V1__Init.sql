--
-- PostgreSQL database dump
--

-- Dumped from database version 14.5
-- Dumped by pg_dump version 14.5

-- Started on 2023-03-06 15:32:18

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 210 (class 1259 OID 51610)
-- Name: customer; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customer (
                                 id bigint NOT NULL,
                                 first_name character varying(255),
                                 last_name character varying(255),
                                 registration_timestamp timestamp without time zone,
                                 user_name character varying(255)
);


ALTER TABLE public.customer OWNER TO postgres;

--
-- TOC entry 209 (class 1259 OID 25523)
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO postgres;

--
-- TOC entry 211 (class 1259 OID 51617)
-- Name: notification_settings; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.notification_settings (
                                              discount boolean,
                                              new_session boolean,
                                              payment_confirmation boolean,
                                              customer_id bigint NOT NULL
);


ALTER TABLE public.notification_settings OWNER TO postgres;

--
-- TOC entry 213 (class 1259 OID 51623)
-- Name: payment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.payment (
                                id bigint NOT NULL,
                                non_discountable_amount numeric(19,2),
                                discountable_amount_no_discount numeric(19,2),
                                discountable_amount_with_discount numeric(19,2),
                                payment_status boolean,
                                total_amount_no_discount numeric(19,2),
                                total_amount_with_discount numeric(19,2),
                                customer_id bigint NOT NULL,
                                session_id bigint NOT NULL
);


ALTER TABLE public.payment OWNER TO postgres;

--
-- TOC entry 212 (class 1259 OID 51622)
-- Name: payment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.payment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.payment_id_seq OWNER TO postgres;

--
-- TOC entry 3373 (class 0 OID 0)
-- Dependencies: 212
-- Name: payment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.payment_id_seq OWNED BY public.payment.id;


--
-- TOC entry 215 (class 1259 OID 51630)
-- Name: product; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product (
                                id bigint NOT NULL,
                                actual boolean,
                                date_created timestamp without time zone NOT NULL,
                                date_updated timestamp without time zone,
                                grindable boolean,
                                name character varying(255),
                                price numeric(19,2),
                                category character varying(255),
                                product_form character varying(255),
                                subcategory character varying(255),
                                mark character varying(255),
                                product_package_id bigint
);


ALTER TABLE public.product OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 51629)
-- Name: product_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.product_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.product_id_seq OWNER TO postgres;

--
-- TOC entry 3374 (class 0 OID 0)
-- Dependencies: 214
-- Name: product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.product_id_seq OWNED BY public.product.id;


--
-- TOC entry 217 (class 1259 OID 51639)
-- Name: product_package; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product_package (
                                        id bigint NOT NULL,
                                        description character varying(255),
                                        weight double precision
);


ALTER TABLE public.product_package OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 51638)
-- Name: product_package_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.product_package_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.product_package_id_seq OWNER TO postgres;

--
-- TOC entry 3375 (class 0 OID 0)
-- Dependencies: 216
-- Name: product_package_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.product_package_id_seq OWNED BY public.product_package.id;


--
-- TOC entry 218 (class 1259 OID 51645)
-- Name: purchase; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.purchase (
                                 id bigint NOT NULL,
                                 count integer,
                                 customer_id bigint NOT NULL,
                                 product_id bigint NOT NULL,
                                 session_id bigint NOT NULL
);


ALTER TABLE public.purchase OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 51651)
-- Name: session; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.session (
                                id bigint NOT NULL,
                                close_notification_sent boolean,
                                closed boolean,
                                coffee_weight double precision,
                                close_date timestamp without time zone,
                                open_date timestamp without time zone,
                                discount integer,
                                discountable_weight double precision,
                                finished boolean,
                                payment_instruction character varying(255),
                                tea_weight double precision,
                                title character varying(255),
                                total_weight double precision
);


ALTER TABLE public.session OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 51650)
-- Name: session_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.session_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.session_id_seq OWNER TO postgres;

--
-- TOC entry 3376 (class 0 OID 0)
-- Dependencies: 219
-- Name: session_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.session_id_seq OWNED BY public.session.id;


--
-- TOC entry 3192 (class 2604 OID 51626)
-- Name: payment id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment ALTER COLUMN id SET DEFAULT nextval('public.payment_id_seq'::regclass);


--
-- TOC entry 3193 (class 2604 OID 51633)
-- Name: product id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product ALTER COLUMN id SET DEFAULT nextval('public.product_id_seq'::regclass);


--
-- TOC entry 3194 (class 2604 OID 51642)
-- Name: product_package id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_package ALTER COLUMN id SET DEFAULT nextval('public.product_package_id_seq'::regclass);


--
-- TOC entry 3195 (class 2604 OID 51654)
-- Name: session id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.session ALTER COLUMN id SET DEFAULT nextval('public.session_id_seq'::regclass);



--
-- TOC entry 3377 (class 0 OID 0)
-- Dependencies: 209
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.hibernate_sequence', 1415, true);


--
-- TOC entry 3378 (class 0 OID 0)
-- Dependencies: 212
-- Name: payment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.payment_id_seq', 41, true);


--
-- TOC entry 3379 (class 0 OID 0)
-- Dependencies: 214
-- Name: product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.product_id_seq', 1240, true);


--
-- TOC entry 3380 (class 0 OID 0)
-- Dependencies: 216
-- Name: product_package_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.product_package_id_seq', 5, true);


--
-- TOC entry 3381 (class 0 OID 0)
-- Dependencies: 219
-- Name: session_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.session_id_seq', 5, true);


--
-- TOC entry 3197 (class 2606 OID 51616)
-- Name: customer customer_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (id);


--
-- TOC entry 3199 (class 2606 OID 51621)
-- Name: notification_settings notification_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notification_settings
    ADD CONSTRAINT notification_settings_pkey PRIMARY KEY (customer_id);


--
-- TOC entry 3201 (class 2606 OID 51628)
-- Name: payment payment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_pkey PRIMARY KEY (id);


--
-- TOC entry 3205 (class 2606 OID 51644)
-- Name: product_package product_package_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_package
    ADD CONSTRAINT product_package_pkey PRIMARY KEY (id);


--
-- TOC entry 3203 (class 2606 OID 51637)
-- Name: product product_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT product_pkey PRIMARY KEY (id);


--
-- TOC entry 3207 (class 2606 OID 51649)
-- Name: purchase purchase_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT purchase_pkey PRIMARY KEY (id);


--
-- TOC entry 3209 (class 2606 OID 51658)
-- Name: session session_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.session
    ADD CONSTRAINT session_pkey PRIMARY KEY (id);


--
-- TOC entry 3214 (class 2606 OID 51679)
-- Name: purchase fk2pehe23hwdcyql94c531rbf70; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT fk2pehe23hwdcyql94c531rbf70 FOREIGN KEY (customer_id) REFERENCES public.customer(id);


--
-- TOC entry 3215 (class 2606 OID 51684)
-- Name: purchase fk3s4jktret4nl7m8yhfc8mfrn5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT fk3s4jktret4nl7m8yhfc8mfrn5 FOREIGN KEY (product_id) REFERENCES public.product(id);


--
-- TOC entry 3216 (class 2606 OID 51689)
-- Name: purchase fka35eeernnp0ndpalxv86dieg8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT fka35eeernnp0ndpalxv86dieg8 FOREIGN KEY (session_id) REFERENCES public.session(id);


--
-- TOC entry 3211 (class 2606 OID 51664)
-- Name: payment fkby2skjf3ov608yb6nm16b49lg; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT fkby2skjf3ov608yb6nm16b49lg FOREIGN KEY (customer_id) REFERENCES public.customer(id);


--
-- TOC entry 3210 (class 2606 OID 51659)
-- Name: notification_settings fkdrywmp53lpmjsn8r9eowipf02; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notification_settings
    ADD CONSTRAINT fkdrywmp53lpmjsn8r9eowipf02 FOREIGN KEY (customer_id) REFERENCES public.customer(id);


--
-- TOC entry 3212 (class 2606 OID 51669)
-- Name: payment fkikhatxlx2rafmi8n9igbsb3x0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT fkikhatxlx2rafmi8n9igbsb3x0 FOREIGN KEY (session_id) REFERENCES public.session(id);


--
-- TOC entry 3213 (class 2606 OID 51674)
-- Name: product fkmq5vdy3wdjawgosane41w4hhv; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT fkmq5vdy3wdjawgosane41w4hhv FOREIGN KEY (product_package_id) REFERENCES public.product_package(id);


-- Completed on 2023-03-06 15:32:18

--
-- PostgreSQL database dump complete
--



