<?php
header('Content-Type: application/json');
include 'config.php';

$data = json_decode(file_get_contents("php://input"), true);

if (isset($data['email']) && isset($data['password'])) {
    $email = $data['email'];
    $password = $data['password'];

    try {
        $stmt = $pdo->prepare("SELECT * FROM users WHERE email = ?");
        $stmt->execute([$email]);
        $user = $stmt->fetch();

        if ($user && password_verify($password, $user['current_password_hash'])) {
            echo json_encode([
                "userId" => $user['id'],
                "masterKeyHash" => $user['master_password_hash']
            ]);
        } else {
            echo json_encode(["error" => "Email o contraseña incorrectos"]);
        }

    } catch (PDOException $e) {
        echo json_encode(["error" => "Error en la base de datos"]);
    }

} else {
    echo json_encode(["error" => "Email y contraseña obligatorios"]);
}


