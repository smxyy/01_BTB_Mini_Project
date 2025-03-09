--
-- PostgreSQL database dump
--

-- Dumped from database version 14.16 (Homebrew)
-- Dumped by pg_dump version 14.16 (Homebrew)

-- Started on 2025-03-09 08:59:13 +07

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
-- TOC entry 210 (class 1259 OID 16438)
-- Name: products; Type: TABLE; Schema: public; Owner: visal
--

CREATE TABLE public.products (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    unit_price numeric(10,2) NOT NULL,
    quantity integer NOT NULL,
    imported_date date NOT NULL
);


ALTER TABLE public.products OWNER TO visal;

--
-- TOC entry 209 (class 1259 OID 16437)
-- Name: products_id_seq; Type: SEQUENCE; Schema: public; Owner: visal
--

CREATE SEQUENCE public.products_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.products_id_seq OWNER TO visal;

--
-- TOC entry 3660 (class 0 OID 0)
-- Dependencies: 209
-- Name: products_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: visal
--

ALTER SEQUENCE public.products_id_seq OWNED BY public.products.id;


--
-- TOC entry 212 (class 1259 OID 16445)
-- Name: stock_tb; Type: TABLE; Schema: public; Owner: visal
--

CREATE TABLE public.stock_tb (
    id integer NOT NULL,
    name character varying(100),
    unit_price numeric(10,2) NOT NULL,
    stock_qty integer NOT NULL,
    import_date date DEFAULT CURRENT_DATE NOT NULL,
    CONSTRAINT stock_tb_stock_qty_check CHECK ((stock_qty >= 0)),
    CONSTRAINT stock_tb_unit_price_check CHECK ((unit_price >= (0)::numeric))
);


ALTER TABLE public.stock_tb OWNER TO visal;

--
-- TOC entry 211 (class 1259 OID 16444)
-- Name: stock_tb_id_seq; Type: SEQUENCE; Schema: public; Owner: visal
--

CREATE SEQUENCE public.stock_tb_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.stock_tb_id_seq OWNER TO visal;

--
-- TOC entry 3661 (class 0 OID 0)
-- Dependencies: 211
-- Name: stock_tb_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: visal
--

ALTER SEQUENCE public.stock_tb_id_seq OWNED BY public.stock_tb.id;


--
-- TOC entry 3503 (class 2604 OID 16477)
-- Name: products id; Type: DEFAULT; Schema: public; Owner: visal
--

ALTER TABLE ONLY public.products ALTER COLUMN id SET DEFAULT nextval('public.products_id_seq'::regclass);


--
-- TOC entry 3505 (class 2604 OID 16478)
-- Name: stock_tb id; Type: DEFAULT; Schema: public; Owner: visal
--

ALTER TABLE ONLY public.stock_tb ALTER COLUMN id SET DEFAULT nextval('public.stock_tb_id_seq'::regclass);


--
-- TOC entry 3652 (class 0 OID 16438)
-- Dependencies: 210
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: visal
--

COPY public.products (id, name, unit_price, quantity, imported_date) FROM stdin;
\.


--
-- TOC entry 3654 (class 0 OID 16445)
-- Dependencies: 212
-- Data for Name: stock_tb; Type: TABLE DATA; Schema: public; Owner: visal
--

COPY public.stock_tb (id, name, unit_price, stock_qty, import_date) FROM stdin;
6	Logitech MX Master 3 Mouse	99.99	180	2025-03-08
7	GoPro HERO11 Black	499.99	60	2025-03-08
8	Microsoft Surface Pro 9	1299.99	40	2025-03-08
10	Bose QuietComfort 45 Headphones	329.99	90	2025-03-08
11	Coca Cola	1.25	5	2025-03-09
5	Nike Air Force	20.00	5	2025-03-08
1	Apple iPhone 14	1000.00	150	2025-03-08
9	Canon EOS R6 Camera	3899.99	30	2025-03-08
\.


--
-- TOC entry 3662 (class 0 OID 0)
-- Dependencies: 209
-- Name: products_id_seq; Type: SEQUENCE SET; Schema: public; Owner: visal
--

SELECT pg_catalog.setval('public.products_id_seq', 1, false);


--
-- TOC entry 3663 (class 0 OID 0)
-- Dependencies: 211
-- Name: stock_tb_id_seq; Type: SEQUENCE SET; Schema: public; Owner: visal
--

SELECT pg_catalog.setval('public.stock_tb_id_seq', 11, true);


--
-- TOC entry 3509 (class 2606 OID 16443)
-- Name: products products_pkey; Type: CONSTRAINT; Schema: public; Owner: visal
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);


--
-- TOC entry 3511 (class 2606 OID 16453)
-- Name: stock_tb stock_tb_pkey; Type: CONSTRAINT; Schema: public; Owner: visal
--

ALTER TABLE ONLY public.stock_tb
    ADD CONSTRAINT stock_tb_pkey PRIMARY KEY (id);


-- Completed on 2025-03-09 08:59:13 +07

--
-- PostgreSQL database dump complete
--

