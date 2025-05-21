<?php
header('Content-Type: application/json');
include 'config.php';

$data = json_decode(file_get_contents("php://input"), true);

if (isset($data['email']) && isset($data['password'])) {
    $email = $data['email'];
    $password = $data['password'];

    // Hashear la contraseña para ambos campos
    $password_hash = password_hash($password, PASSWORD_DEFAULT);
    $master_key = openssl_random_pseudo_bytes(32);
    $master_key_base64 = base64_encode($master_key); //lo necesita android

    try {
        $stmt = $pdo->prepare("INSERT INTO users (email, current_password_hash, master_password_hash) VALUES (?, ?, ?)");
        $stmt->execute([$email, $password_hash, $master_key_base64]);

        echo json_encode(["success" => "Usuario registrado"]);
    } catch (PDOException $e) {
        if ($e->getCode() == 23000) {
            echo json_encode(["error" => "El email ya está registrado"]);
        } else {
            echo json_encode(["error" => "Error en la base de datos"]);
        }
    }

} else {
    echo json_encode(["error" => "Email y contraseña obligatorios"]);
}
