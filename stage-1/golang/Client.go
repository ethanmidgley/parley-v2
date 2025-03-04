go package main

import (
	"bufio"
	"fmt"
	"net"
	"os"
	"strconv"
	"strings"
)

func server(port int) {

	ln, err := net.Listen("tcp", "localhost:"+strconv.Itoa(port))
	if err != nil {
		fmt.Println(err)
		return
	}

	conn, err := ln.Accept()
	if err != nil {
		fmt.Println(err)
		return
	}

	defer conn.Close()

	for {

		buf := make([]byte, 256)
		_, err = conn.Read(buf)

		if err != nil {
			fmt.Println(err)
			return
		}

		fmt.Printf("\u001b[2K\r%s: %s", conn.RemoteAddr().String(), buf)
		fmt.Print("You: ")

		_, err = conn.Write(buf)
		if err != nil {
			fmt.Println(err)
			return
		}
	}

}

func main() {

	server_port, err := strconv.Atoi(os.Args[1])
	if err != nil {
		fmt.Println(err)
		return
	}
	go server(server_port)

	reader := bufio.NewReader(os.Stdin)

	var conn net.Conn
	fmt.Println("Please enter client address and port (eg 192.168.1.3:6911) - ")
	for conn == nil {

		line, err := reader.ReadString('\n')
		if err != nil {
			fmt.Println(err)
			return
		}
		line = strings.TrimSpace(line)

		parts := strings.Split(line, ":")

		if len(parts) != 2 {
			fmt.Println("Invalid server address")
			continue
		}

		if net.ParseIP(parts[0]).To4() == nil {
			fmt.Println("Invalid ip address")
			continue
		}

		p, err := strconv.Atoi(parts[1])
		if err != nil {
			fmt.Println("Invalid port number")
			continue
		}

		conn, err = net.Dial("tcp", parts[0]+":"+strconv.Itoa(p))
		if err != nil {
			fmt.Println("Unknown host")
			return
		}

	}

	writer := bufio.NewWriter(conn)

	for {
		fmt.Print("You: ")
		input, _ := reader.ReadString('\n')

		if input == "exit" {
			break
		}

		_, err := writer.Write([]byte(input))
		if err != nil {
			fmt.Println(err)
			return
		}
		writer.Flush()
	}

}
